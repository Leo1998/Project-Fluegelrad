package de.doaktiv.fragments.calendar.listview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.doaktiv.MainActivity;
import de.doaktiv.R;
import de.doaktiv.database.DatabaseDownloadTask;
import de.doaktiv.database.DatabaseTaskWatcher;
import de.doaktiv.fragments.RecyclerViewAdapter;

public class CalendarListFragment extends Fragment {

    private RecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.calender_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        this.adapter = new RecyclerViewAdapter(false);
        adapter.setActivity(getActivity());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) CalendarListFragment.this.getActivity()).getDatabaseManager().executeTask(new DatabaseDownloadTask(), null, new DatabaseTaskWatcher() {
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

    public void refreshData() {
        if (this.adapter != null && this.getView() != null) {
            this.getView().post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
