package de.projectfluegelrad.calendar.listview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalenderListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.calender_list_fragment, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        List<Event> events = new ArrayList<>();
        List<Event> original = getArguments().getParcelableArrayList("eventList");

        for (int i = original.size()-1; i >= 0; i--){
            if (original.get(i).getDate().compareTo(new Date(System.currentTimeMillis())) > 0){
                events.add(original.get(i));
            }else {
                break;
            }
        }

        Collections.reverse(events);


        CalenderRecyclerViewAdapter adapter = new CalenderRecyclerViewAdapter(events);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }
}
