package de.doaktiv.android.fragments.calendar.listview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.RecyclerViewAdapter;
import de.doaktiv.database.DatabaseDownloadTask;
import de.doaktiv.database.DatabaseTaskWatcher;

public class CalendarListFragment extends DoaktivFragment {

    private RecyclerViewAdapter adapter;

    @Override
    public View createView(Context context) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) inflater().inflate(R.layout.calender_list_fragment, null, false);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        this.adapter = new RecyclerViewAdapter();
        adapter.setController(getFragmentController());
        adapter.setEventList(database.getRecentEventList());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CalendarListFragment.this.getApplication().getDatabaseManager().executeTask(new DatabaseDownloadTask(), null, new DatabaseTaskWatcher() {
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
        if (this.getFragmentView() != null) {
            this.getFragmentView().post(new Runnable() {
                @Override
                public void run() {
                    adapter.setEventList(database.getHomeEventList());
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
