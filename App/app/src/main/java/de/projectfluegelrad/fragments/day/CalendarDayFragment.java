package de.projectfluegelrad.fragments.day;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import de.projectfluegelrad.BuildConfig;
import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.database.Sponsor;
import de.projectfluegelrad.fragments.calendar.SponsorView;

public class CalendarDayFragment extends Fragment {

    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // setup osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(getContext().getCacheDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath(), "tiles"));

        this.event = DatabaseManager.INSTANCE.getEvent(getArguments().getInt("eventId"));

        CoordinatorLayout layout = (CoordinatorLayout) inflater.inflate(R.layout.calendar_day_fragment, container, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar_day_fragment);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        buildMenu(toolbar);

        assignEventData(layout);

        return layout;
    }

    private void assignEventData(CoordinatorLayout layout) {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) layout.findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(event.getName());

        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        ((TextView) layout.findViewById(R.id.date)).setText(event.getDateStartFormatted());

        ViewPager pager = (ViewPager) layout.findViewById(R.id.image_pager);
        buildImagePager(pager);

        CardView sponsorsCard = (CardView) layout.findViewById(R.id.sponsors_container);
        buildSponsorsContainer(sponsorsCard);

        MapView mapView = (MapView) layout.findViewById(R.id.mapView);
        buildMapView(mapView);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    private void buildMenu(Toolbar toolbar) {
        toolbar.inflateMenu(R.menu.day_view_menu);

        Menu menu = toolbar.getMenu();

        // tint icons
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
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

    private void buildImagePager(ViewPager pager) {
        List<Image> images = DatabaseManager.INSTANCE.getImages(this.event);

        ImagePagerAdapter adapter = new ImagePagerAdapter(getFragmentManager(), images);
        pager.setAdapter(adapter);
    }

    private void buildSponsorsContainer(CardView sponsorsCard) {
        LinearLayout sponsorsContainer = new LinearLayout(sponsorsCard.getContext());
        sponsorsContainer.setOrientation(LinearLayout.VERTICAL);
        sponsorsContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);//TODO dividers not working

        //build host
        TextView hostTitle = new TextView(this.getContext());
        hostTitle.setText(R.string.host_title);
        sponsorsContainer.addView(hostTitle);

        SponsorView hostView = new SponsorView(this.getContext(), this.getActivity());
        hostView.setSponsor(DatabaseManager.INSTANCE.getSponsor(event.getHostId()));

        sponsorsContainer.addView(hostView);

        //build other sponsors
        List<Sponsor> sponsors = DatabaseManager.INSTANCE.getSponsors(event);
        if (!sponsors.isEmpty()) {
            TextView sponsorsTitle = new TextView(this.getContext());
            sponsorsTitle.setText(R.string.sponsors_title);
            sponsorsContainer.addView(sponsorsTitle);

            for (Sponsor sponsor : sponsors) {
                SponsorView sponsorView = new SponsorView(this.getContext(), this.getActivity());
                sponsorView.setSponsor(sponsor);

                sponsorsContainer.addView(sponsorView);
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

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getContext(), items,
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
        startActivity(sendIntent);
    }

    private void addToCalendar() {
        Intent intent = new Intent(Intent.ACTION_INSERT);

        intent.setData(CalendarContract.Events.CONTENT_URI);

        intent.putExtra(CalendarContract.Events.TITLE, event.getName());
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getDateStart().getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getDateEnd().getTimeInMillis());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + CalendarDayFragment.this.getString(R.string.calender_organized_by) + " " + DatabaseManager.INSTANCE.getSponsor(event.getHostId()).getName());//TODO
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

        CalendarDayFragment.this.startActivity(intent);
    }

}
