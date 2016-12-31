package de.projectfluegelrad.fragments.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.MainActivity;
import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.DatabaseRequest;
import de.projectfluegelrad.database.DatabaseRequestListener;
import de.projectfluegelrad.database.Event;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.home_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        List<Event> events = new ArrayList<>();
        List<Event> original = DatabaseManager.INSTANCE.getEventList();

        for (int i = original.size()-1; i >= 0; i--){
            if (original.get(i).getDateStart().compareTo(Calendar.getInstance()) > 0){
                events.add(original.get(i));
            }else {
                break;
            }
        }

        Collections.reverse(events);

        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(events);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) HomeFragment.this.getActivity()).getDatabaseManager().request(DatabaseRequest.RefreshEventList, false, new DatabaseRequestListener() {
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
