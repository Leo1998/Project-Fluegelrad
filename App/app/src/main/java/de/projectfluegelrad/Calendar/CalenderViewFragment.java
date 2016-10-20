package de.projectfluegelrad.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderViewFragment extends Fragment {

    private List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = getArguments().getParcelableArrayList("eventList");

        CalenderView calenderView = (CalenderView) inflater.inflate(R.layout.calender_grid_fragment, container, false);
        calenderView.setEvents(events);

        return calenderView;
    }
}
