package de.doaktiv.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import de.doaktiv.database.Database;
import de.doaktiv.database.DatabaseReceiver;

public abstract class DoaktivFragment extends Fragment implements DatabaseReceiver {

    private static final String TAG = "DoaktivFragment";

    private DoaktivApplication application;
    private RootController rootController;

    protected Database database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (DoaktivApplication) getActivity().getApplication();
        this.rootController = ((DoaktivActivity) getActivity()).getController();

        this.application.registerReceiver(this);
        this.onReceive(this.application.getDatabaseManager().getDatabase()); //
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.application.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Database database) {
        this.database = database;

        Log.i(TAG, "onRefreshLayout");
        onRefreshLayout();
    }

    protected abstract void onRefreshLayout();

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public DoaktivApplication getApplication() {
        return application;
    }

    public DoaktivActivity getDoaktivActivity() {
        return (DoaktivActivity) getActivity();
    }

    public RootController getRootController() {
        return rootController;
    }
}
