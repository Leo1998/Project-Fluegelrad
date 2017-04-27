package de.doaktiv.android.fragments.eventlist;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.RecyclerViewAdapter;
import de.doaktiv.database.Database;

public class CalendarListFragment extends DoaktivFragment {

    private RecyclerViewAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View createView(Context context) {
        this.swipeRefreshLayout = (SwipeRefreshLayout) inflater().inflate(R.layout.calender_list_fragment, null, false);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        this.adapter = new RecyclerViewAdapter();
        adapter.setController(getRootController());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CalendarListFragment.this.getRootController().getDatabaseService().downloadDatabase(new Runnable() {
                    @Override
                    public void run() {
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
    public void onPause() {
        super.onPause();

        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.clearAnimation();
        }
    }

    @Override
    public void onDatabaseReceived(final Database database) {
        if (this.getFragmentView() != null) {
            this.getFragmentView().post(new Runnable() {
                @Override
                public void run() {
                    adapter.setEventList(database.getHomeEventList());
                }
            });
        }
    }
}
