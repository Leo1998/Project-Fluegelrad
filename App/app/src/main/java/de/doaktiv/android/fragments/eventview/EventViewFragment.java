package de.doaktiv.android.fragments.eventview;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import de.doaktiv.android.base.Toolbar;
import de.doaktiv.android.fragments.SponsorView;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;
import de.doaktiv.database.Sponsor;

public class EventViewFragment extends DoaktivFragment {

    private FrameLayout layout;

    private Event event;
    private Database database;//bad style

    @Override
    public void onFragmentCreate() {
        super.onFragmentCreate();

        // setup osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(getApplication().getCacheDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath(), "tiles"));
    }

    @Override
    public View createView(Context context) {
        this.layout = (FrameLayout) inflater().inflate(R.layout.event_view_fragment, null, false);

        return layout;
    }

    @Override
    public void onDatabaseReceived(final Database database) {
        if (this.getFragmentView() != null) {
            this.getFragmentView().post(new Runnable() {
                @Override
                public void run() {
                    EventViewFragment.this.database = database;
                    EventViewFragment.this.event = database.getEvent(getArguments().getInt("eventId"));

                    assignEventData();
                }
            });
        }
    }

    private void assignEventData() {
        Toolbar toolbar = this.getToolbar();
        if (toolbar != null) {
            toolbar.setTitleText(event.getName());
        }

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

        CardView imageSliderCard = (CardView) layout.findViewById(R.id.image_slider_container);
        buildImageSlider(imageSliderCard);

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

        layout.findViewById(R.id.shareFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        layout.findViewById(R.id.addToCalendarFab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCalendar();
            }
        });
    }

    private void buildImageSlider(CardView imageSliderCard) {
        if (event.getImages().size() == 0) {
            imageSliderCard.setVisibility(View.GONE);

            return;
        }

        ViewPager slider = (ViewPager) imageSliderCard.findViewById(R.id.image_slider);

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
        getApplication().startActivity(sendIntent);
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT);

        intent.setData(CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDateStart().getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getDateEnd().getTimeInMillis());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + getResources().getString(R.string.calender_organized_by) + " " + database.getSponsor(event.getHostId()).getName());//TODO
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

        getApplication().startActivity(intent);
    }

}
