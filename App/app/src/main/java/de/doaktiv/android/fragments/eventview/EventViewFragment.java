package de.doaktiv.android.fragments.eventview;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.doaktiv.BuildConfig;
import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.calendar.SponsorView;
import de.doaktiv.database.Event;
import de.doaktiv.database.Sponsor;

public class EventViewFragment extends DoaktivFragment {

    private Event event;

    @Override
    public View createView(Context context) {
        // setup osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(context.getCacheDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath(), "tiles"));

        this.event = database.getEvent(getArguments().getInt("eventId"));

        CoordinatorLayout layout = (CoordinatorLayout) inflater().inflate(R.layout.calendar_day_fragment, null, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar_day_fragment);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRootController().doSystemBack();
            }
        });

        buildMenu(toolbar);

        assignEventData(layout);

        return layout;
    }

    private void assignEventData(CoordinatorLayout layout) {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) layout.findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(event.getName());

        //stupid bug fix
        layout.findViewById(R.id.scroll_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        layout.findViewById(R.id.image_slider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ViewPager slider = (ViewPager) layout.findViewById(R.id.image_slider);
        buildImageSlider(slider);

        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        ((TextView) layout.findViewById(R.id.date)).setText(getResources().getString(R.string.date) + ": " + event.getDateStartFormatted() + " (" + getResources().getString(R.string.duration) + ": " + event.getDurationFormatted() + ")");

        ((TextView) layout.findViewById(R.id.age)).setText(getResources().getString(R.string.age) + ": " + event.getAgeMin() + " - " + event.getAgeMax());

        CardView sponsorsCard = (CardView) layout.findViewById(R.id.sponsors_container);
        buildSponsorsContainer(sponsorsCard);

        ((TextView) layout.findViewById(R.id.location)).setText(getResources().getString(R.string.address) + ": " + event.getLocation().getAddress());

        MapView mapView = (MapView) layout.findViewById(R.id.mapView);
        buildMapView(mapView);

        layout.findViewById(R.id.participateFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRootController().openParticipateView(event.getId());
            }
        });
    }

    @Override
    protected de.doaktiv.android.base.Toolbar createToolbar(Context context) {
        return null;
    }

    @Override
    protected void onRefreshLayout() {
    }

    private void buildMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.day_view_menu);

        Menu menu = toolbar.getMenu();

        // tint icons
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }
        }

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.share) {
                    share();
                } else if (id == R.id.add_calendar) {
                    addToCalendar();
                }

                return true;
            }
        });
    }

    private void buildImageSlider(ViewPager slider) {
        ImageSliderAdapter adapter = new ImageSliderAdapter(slider.getContext(), event);
        slider.setAdapter(adapter);
    }

    private void buildSponsorsContainer(CardView sponsorsCard) {
        LinearLayout sponsorsContainer = new LinearLayout(sponsorsCard.getContext());
        sponsorsContainer.setOrientation(LinearLayout.VERTICAL);
        sponsorsContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);//TODO dividers not working

        //build host
        TextView hostTitle = new TextView(sponsorsCard.getContext());
        hostTitle.setText(R.string.host_title);
        sponsorsContainer.addView(hostTitle);

        SponsorView hostView = new SponsorView(sponsorsCard.getContext(), this.getRootController());
        hostView.setSponsor(database.getSponsor(event.getHostId()));

        sponsorsContainer.addView(hostView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //build other sponsors
        List<Sponsor> sponsors = database.getSponsors(event);
        if (!sponsors.isEmpty()) {
            TextView sponsorsTitle = new TextView(sponsorsCard.getContext());
            sponsorsTitle.setText(R.string.sponsors_title);
            sponsorsContainer.addView(sponsorsTitle);

            for (Sponsor sponsor : sponsors) {
                SponsorView sponsorView = new SponsorView(sponsorsCard.getContext(), this.getRootController());
                sponsorView.setSponsor(sponsor);

                sponsorsContainer.addView(sponsorView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(6, 6, 6, 6);
        sponsorsCard.addView(sponsorsContainer, params);
    }

    private void buildMapView(MapView mapView) {
        mapView.setMultiTouchControls(true);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        {
            double latitude = event.getLocation().getLatitude();
            double longitude = event.getLocation().getLongitude();

            IMapController mapController = mapView.getController();
            mapController.setZoom(90);
            GeoPoint point = new GeoPoint(latitude, longitude);
            mapController.setCenter(point);

            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            items.add(new OverlayItem(event.getName(), "", point));

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(mapView.getContext(), items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            return false;
                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    });

            mOverlay.setFocusItemsOnTap(true);

            mapView.getOverlays().add(mOverlay);
        }
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "N/A");//TODO!!!
        sendIntent.setType("text/plain");
        //startActivity(sendIntent);
        //TODO
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT);

        intent.setData(CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDateStart().getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getDateEnd().getTimeInMillis());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + getResources().getString(R.string.calender_organized_by) + " " + database.getSponsor(event.getHostId()).getName());//TODO
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

        //EventViewFragment.this.startActivity(intent);
        //TODO
    }

}
