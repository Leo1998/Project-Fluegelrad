package de.doaktiv.android.fragments.home;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.eventlist.EventListView;
import de.doaktiv.database.Database;

public class HomeFragment extends DoaktivFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private EventListView eventListView;

    @Override
    public View createView(Context context) {
        this.swipeRefreshLayout = (SwipeRefreshLayout) inflater().inflate(R.layout.home_fragment, null, false);

        this.eventListView = (EventListView) swipeRefreshLayout.findViewById(R.id.event_list);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.this.getRootController().getDatabaseService().downloadDatabase(new Runnable() {
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
    protected void createToolbar(Context context) {
        super.createToolbar(context);

        this.getToolbar().setSearchButtonEnabled(true);
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
                    eventListView.setEventList(database.getHomeEventList(), database, getRootController());
                }
            });
        }
    }
}
