package de.doaktiv.android.fragments.calendar.gridview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.android.DoaktivFragment;
import de.doaktiv.database.DatabaseDownloadTask;
import de.doaktiv.database.DatabaseTaskWatcher;
import de.doaktiv.database.Event;

public class CalendarGridViewFragment extends DoaktivFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.calender_grid_fragment, container, false);

        final CalendarGridView calendarGridView = (CalendarGridView) swipeRefreshLayout.findViewById(R.id.calendar_view);
        calendarGridView.setDatabase(database);

        GridView gridView = (GridView) calendarGridView.findViewById(R.id.calendar_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<Event> eventsOnDate = new ArrayList<Event>();

                for (Event e : database.getEventList()) {
                    Calendar calendar = e.getDateStart();

                    if (calendar.get(Calendar.YEAR) == calendarGridView.getDaysShown().get(i).get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == calendarGridView.getDaysShown().get(i).get(Calendar.MONTH) && calendar.get(Calendar.DAY_OF_MONTH) == calendarGridView.getDaysShown().get(i).get(Calendar.DAY_OF_MONTH)) {
                        eventsOnDate.add(e);
                    }
                }

                if (eventsOnDate.size() == 1) {
                    getRootController().openEventView(eventsOnDate.get(0).getId());
                }
                if (eventsOnDate.size() > 1) {
                    //TODO
                    /*DialogFragment newFragment = new CalendarDayDialog();

                    Bundle bundle = new Bundle();
                    int[] array = new int[eventsOnDate.size()];
                    for (int j = 0; j < array.length; j++) {
                        array[j] = eventsOnDate.get(j).getId();
                    }
                    bundle.putIntArray("eventIds", array);

                    newFragment.setArguments(bundle);
                    newFragment.show(CalendarGridViewFragment.this.getActivity().getSupportFragmentManager(), "eventsOnDateDialog");*/
                }

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((DoaktivActivity) CalendarGridViewFragment.this.getActivity()).getDatabaseManager().executeTask(new DatabaseDownloadTask(), null, new DatabaseTaskWatcher() {
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

    @Override
    protected void onRefreshLayout() {

    }
}
