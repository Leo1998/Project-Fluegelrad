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

public class CalendarDayFragment extends Fragment{

    private Event event;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        event = getArguments().getParcelable("event");

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.calender_day_fragment, container, false);

        ((TextView) layout.findViewById(R.id.category)).setText(event.getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        ((TextView) layout.findViewById(R.id.date)).setText(simpleDateFormat.format(event.getDate().getTime()));

        ((TextView) layout.findViewById(R.id.host)).setText("N/A");

        RelativeLayout descriptionContainer = (RelativeLayout)  layout.findViewById(R.id.description_container);
        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        ((TextView) layout.findViewById(R.id.location)).setText(event.getLocation());

        /*layout.findViewById(R.id.location_button).setOnClickListener(view -> {
            //TODO:Location
            Uri uri = Uri.parse("geo:0,0?q=" + event.getLocation());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });*/

        mapView = (MapView) layout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.getUiSettings().setMyLocationButtonEnabled(false);

                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                Address address = null;
                try {
                    List<Address> addresses = geocoder.getFromLocationName(event.getLocation(), 1);
                    if (addresses.size() > 0) {
                        address = addresses.get(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (address != null) {
                    map.addMarker(new MarkerOptions().draggable(true).position(new LatLng(address.getLatitude(), address.getLongitude())).title("Marker"));
                }
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
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());

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
