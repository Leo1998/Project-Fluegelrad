package de.projectfluegelrad.calendar.gridview;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.MainActivity;
import de.projectfluegelrad.R;
import de.projectfluegelrad.calendar.CalenderDayFragment;
import de.projectfluegelrad.database.Event;

public class CalenderGridViewFragment extends Fragment {

    private List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = getArguments().getParcelableArrayList("eventList");

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)inflater.inflate(R.layout.calender_grid_fragment, container, false);

        CalenderGridView calenderGridView = (CalenderGridView) swipeRefreshLayout.findViewById(R.id.calendar_view);
        calenderGridView.setEvents(events);

        GridView gridView = (GridView) calenderGridView.findViewById(R.id.calendar_grid);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Calendar calendar = Calendar.getInstance();

            List<Event> eventsOnDate = new ArrayList<Event>();

            for (Event e : events){
                calendar.setTime(e.getDate());

                if (calendar.get(Calendar.YEAR) == calenderGridView.getDaysShown().get(i).get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calenderGridView.getDaysShown().get(i).get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == calenderGridView.getDaysShown().get(i).get(Calendar.DAY_OF_MONTH)){
                    eventsOnDate.add(e);
                }
            }

            if (eventsOnDate.size() == 1){
                Bundle bundle = new Bundle();
                bundle.putParcelable("event", eventsOnDate.get(0));

                CalenderDayFragment calenderDayFragment = new CalenderDayFragment();
                calenderDayFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calenderDayFragment).addToBackStack("calenderDayFragment").commit();
            }
            if (eventsOnDate.size() > 1){
                DialogFragment newFragment = new CalenderDayDialog();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("eventList", (ArrayList<? extends Parcelable>) eventsOnDate);

                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "eventsOnDateDialog");
            }

        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            ((MainActivity) getActivity()).refreshData();

            swipeRefreshLayout.setRefreshing(false);
        });

        return swipeRefreshLayout;
    }
}
