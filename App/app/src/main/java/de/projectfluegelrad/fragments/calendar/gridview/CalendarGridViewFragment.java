package de.projectfluegelrad.fragments.calendar.gridview;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.projectfluegelrad.MainActivity;
import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseDownloadTask;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.DatabaseTaskWatcher;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.fragments.day.CalendarDayFragment;

public class CalendarGridViewFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final List<Event> events = DatabaseManager.INSTANCE.getEventList();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)inflater.inflate(R.layout.calender_grid_fragment, container, false);

        final CalendarGridView calendarGridView = (CalendarGridView) swipeRefreshLayout.findViewById(R.id.calendar_view);
        calendarGridView.setEvents(events);

        GridView gridView = (GridView) calendarGridView.findViewById(R.id.calendar_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Event> eventsOnDate = new ArrayList<Event>();

                for (Event e : events) {
                    Calendar calendar = e.getDateStart();

                    if (calendar.get(Calendar.YEAR) == calendarGridView.getDaysShown().get(i).get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calendarGridView.getDaysShown().get(i).get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == calendarGridView.getDaysShown().get(i).get(Calendar.DAY_OF_MONTH)) {
                        eventsOnDate.add(e);
                    }
                }

                if (eventsOnDate.size() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("eventId", eventsOnDate.get(0).getId());

                    CalendarDayFragment calendarDayFragment = new CalendarDayFragment();
                    calendarDayFragment.setArguments(bundle);

                    CalendarGridViewFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarDayFragment).addToBackStack("calendarDayFragment").commit();
                }
                if (eventsOnDate.size() > 1) {
                    DialogFragment newFragment = new CalendarDayDialog();

                    Bundle bundle = new Bundle();
                    int[] array = new int[eventsOnDate.size()];
                    for (int j = 0; j < array.length; j++) {
                        array[j] = eventsOnDate.get(j).getId();
                    }
                    bundle.putIntArray("eventIds", array);

                    newFragment.setArguments(bundle);
                    newFragment.show(CalendarGridViewFragment.this.getActivity().getSupportFragmentManager(), "eventsOnDateDialog");
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) CalendarGridViewFragment.this.getActivity()).getDatabaseManager().executeTask(new DatabaseDownloadTask(), null, new DatabaseTaskWatcher() {
                    @Override
                    public void onFinish(Object result) {
                        swipeRefreshLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });

        return swipeRefreshLayout;
    }
}
