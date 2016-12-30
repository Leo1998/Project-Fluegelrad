package de.projectfluegelrad.calendar;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.projectfluegelrad.BuildConfig;
import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.database.ImageAtlas;

public class CalendarDayFragment extends Fragment {

    private Event event;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // setup osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(getContext().getCacheDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath(), "tiles"));

        List<Event> events = DatabaseManager.INSTANCE.getEventList();
        for (Event e : events) {
            if (e.getId() == getArguments().getInt("eventId")) {
                this.event = e;
            }
        }

        this.setHasOptionsMenu(true);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.calender_day_fragment, container, false);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        ((TextView) layout.findViewById(R.id.date)).setText(simpleDateFormat.format(event.getDateStart().getTime()));

        ((TextView) layout.findViewById(R.id.name)).setText(event.getName());

        ((TextView) layout.findViewById(R.id.host)).setText("N/A");

        RelativeLayout scrollContainer = (RelativeLayout)  layout.findViewById(R.id.scroll_container);
        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        LinearLayout imagesContainer = (LinearLayout) layout.findViewById(R.id.images_container);
        ImageAtlas imageAtlas = DatabaseManager.INSTANCE.getImageAtlas();
        List<Image> images = imageAtlas.getImages(this.event);
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            ImageHolder imageView = new ImageHolder(this.getContext(), image);

            imagesContainer.addView(imageView.getLayout());
        }

        ((TextView) layout.findViewById(R.id.location)).setText(event.getLocation().getAddress());

        mapView = (MapView) layout.findViewById(R.id.mapView);
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

        //TODO:Sponsoren
        ((TextView) layout.findViewById(R.id.sponsors)).setText("Sponsoren");

        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.day_view_menu, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            share();
        } else if (id == R.id.add_calendar) {
            addToCalendar();
        }

        return super.onOptionsItemSelected(item);
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
        intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + CalendarDayFragment.this.getString(R.string.calender_organized_by) + " " + event.getHost());
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

        CalendarDayFragment.this.startActivity(intent);
    }

}
