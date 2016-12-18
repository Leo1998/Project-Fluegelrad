package de.projectfluegelrad.calendar;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.database.ImageAtlas;

public class CalendarDayFragment extends Fragment{

    private Event event;
    private ImageAtlas imageAtlas;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.event = getArguments().getParcelable("event");
        this.imageAtlas = getArguments().getParcelable("imageAtlas");

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.calender_day_fragment, container, false);

        ((TextView) layout.findViewById(R.id.category)).setText(event.getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        ((TextView) layout.findViewById(R.id.date)).setText(simpleDateFormat.format(event.getDate().getTime()));

        ((TextView) layout.findViewById(R.id.host)).setText("N/A");

        RelativeLayout scrollContainer = (RelativeLayout)  layout.findViewById(R.id.scroll_container);
        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        LinearLayout imagesContainer = (LinearLayout) layout.findViewById(R.id.images_container);
        List<Image> images = imageAtlas.getImages(this.event);
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            ImageView imageView = new ImageView(this.getContext());
            imageView.setImageBitmap(image.getBitmap());

            imagesContainer.addView(imageView);
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

        layout.findViewById(R.id.calender_add_button).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);

            intent.setData(CalendarContract.Events.CONTENT_URI);

            Calendar day = event.getDate();

            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, day.getTimeInMillis());
            //TODO:Ende des Events
            //day.setTime(event.getEndDate());
            day.add(Calendar.HOUR, 2);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,day.getTimeInMillis());
            intent.putExtra(CalendarContract.Events.TITLE, event.getCategory());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription() + "\n " + getString(R.string.calender_organized_by) + " " + event.getHost());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation().getAddress());

            startActivity(intent);
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
