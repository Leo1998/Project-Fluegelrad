package de.projectfluegelrad.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.projectfluegelrad.R;

public class CalenderListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = ((RecyclerView) inflater.inflate(R.layout.calender_list_fragment, container, false));
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        CalenderRecyclerViewAdapter adapter = new CalenderRecyclerViewAdapter(getArguments().getParcelableArrayList("eventList"));
        recyclerView.setAdapter(adapter);


        return recyclerView;
    }
}
