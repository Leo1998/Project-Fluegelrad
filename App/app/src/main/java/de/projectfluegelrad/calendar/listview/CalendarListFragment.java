package de.projectfluegelrad.calendar.listview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.MainActivity;
import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseRequest;
import de.projectfluegelrad.database.DatabaseRequestListener;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.ImageAtlas;

public class CalendarListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.calender_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        List<Event> events = new ArrayList<>();
        List<Event> original = getArguments().getParcelableArrayList("eventList");

        for (int i = original.size()-1; i >= 0; i--){
            if (original.get(i).getDate().compareTo(Calendar.getInstance()) > 0){
                events.add(original.get(i));
            }else {
                break;
            }
        }

        Collections.reverse(events);

        ImageAtlas imageAtlas = getArguments().getParcelable("imageAtlas");

        CalendarRecyclerViewAdapter adapter = new CalendarRecyclerViewAdapter(events, imageAtlas);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) CalendarListFragment.this.getActivity()).getDatabaseManager().request(DatabaseRequest.RefreshEventList, false, new DatabaseRequestListener() {
                    @Override
                    public void onFinish() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        return swipeRefreshLayout;
    }
}
