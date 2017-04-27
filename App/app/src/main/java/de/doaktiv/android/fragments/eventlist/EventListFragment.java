package de.doaktiv.android.fragments.eventlist;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.database.Database;

public class EventListFragment extends DoaktivFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private EventListView eventListView;

    @Override
    public View createView(Context context) {
        this.swipeRefreshLayout = (SwipeRefreshLayout) inflater().inflate(R.layout.event_list_fragment, null, false);

        this.eventListView = (EventListView) swipeRefreshLayout.findViewById(R.id.event_list);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventListFragment.this.getRootController().getDatabaseService().downloadDatabase(new Runnable() {
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
                    eventListView.setEventList(database.getRecentEventList(), getRootController());
                }
            });
        }
    }
}
