package de.projectfluegelrad.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.database.ImageAtlas;

public class CalendarDayFragment extends Fragment{

    private Event event;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        List<Event> events = DatabaseManager.INSTANCE.getEventList();
        for (Event e : events) {
            if (e.getId() == getArguments().getInt("eventId")) {
                this.event = e;
            }
        }

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.calender_day_fragment, container, false);

        ((TextView) layout.findViewById(R.id.category)).setText(event.getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        ((TextView) layout.findViewById(R.id.date)).setText(simpleDateFormat.format(event.getDate().getTime()));

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
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.getUiSettings().setMyLocationButtonEnabled(false);

                double latitude = event.getLocation().getLatitude();
                double longitude = event.getLocation().getLongitude();

                map.addMarker(new MarkerOptions().draggable(true).position(new LatLng(latitude, longitude)).title("Marker"));

                CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
                map.animateCamera(cam);
            }
        });

        layout.findViewById(R.id.calender_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);

                intent.setData(CalendarContract.Events.CONTENT_URI);

                Calendar day = event.getDate();

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, day.getTimeInMillis());
                //TODO:Ende des Events
                //day.setTime(event.getEndDate());
                day.add(Calendar.HOUR, 2);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, day.getTimeInMillis());
                intent.putExtra(CalendarContract.Events.TITLE, event.getCategory());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + CalendarDayFragment.this.getString(R.string.calender_organized_by) + " " + event.getHost());
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

                CalendarDayFragment.this.startActivity(intent);
            }
        });

        //TODO:Sponsoren
        ((TextView) layout.findViewById(R.id.sponsors)).setText("Sponsoren");

        return layout;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
