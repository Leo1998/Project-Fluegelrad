package de.projectfluegelrad.calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderGridViewFragment extends Fragment {

    private List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = getArguments().getParcelableArrayList("eventList");

        CalenderGridView calenderGridView = (CalenderGridView) inflater.inflate(R.layout.calender_grid_fragment, container, false);
        calenderGridView.setEvents(events);

        return calenderGridView;
    }
}
