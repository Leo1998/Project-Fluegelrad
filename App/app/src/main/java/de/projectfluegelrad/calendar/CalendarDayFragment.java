package de.projectfluegelrad.calendar;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalendarDayFragment extends Fragment{

    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        event = getArguments().getParcelable("event");

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.calender_day_fragment, container, false);

        ((TextView) layout.findViewById(R.id.category)).setText(event.getCategory());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("E  dd.MM.yyyy HH:mm");
        ((TextView) layout.findViewById(R.id.date)).setText(simpleDateFormat.format(event.getDate()));

        ((TextView) layout.findViewById(R.id.host)).setText(event.getHost());

        RelativeLayout descriptionContainer = (RelativeLayout)  layout.findViewById(R.id.description_container);
        ((TextView) layout.findViewById(R.id.description)).setText(event.getDescription());

        ((TextView) layout.findViewById(R.id.location)).setText(event.getLocation());

        ((Button) layout.findViewById(R.id.location_button)).setOnClickListener(view -> {
            //TODO:Location
            Uri uri = Uri.parse("geo:0,0?q=" + event.getLocation());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        ((Button) layout.findViewById(R.id.calender_add_button)).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);

            intent.setData(CalendarContract.Events.CONTENT_URI);

            Calendar day = Calendar.getInstance();
            day.setTime(event.getDate());

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
}
