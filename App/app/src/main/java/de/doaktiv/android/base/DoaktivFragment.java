package de.doaktiv.android.base;

import android.os.Bundle;
import android.util.Log;

import de.doaktiv.android.DoaktivApplication;
import de.doaktiv.android.RootController;
import de.doaktiv.database.Database;
import de.doaktiv.database.DatabaseReceiver;

public abstract class DoaktivFragment extends BaseFragment implements DatabaseReceiver {

    private static final String TAG = "DoaktivFragment";

    private DoaktivApplication application;
    protected Database database;

    public DoaktivFragment() {
        super();
    }

    public DoaktivFragment(Bundle args) {
        super(args);
    }

    @Override
    public void onFragmentCreate() {
        super.onFragmentCreate();

        this.application = (DoaktivApplication) getFragmentController().getApplication();

        if (getToolbar() != null) {
            getToolbar().setTitleText("DO Aktiv");
        }

        this.application.registerReceiver(this);
        this.onReceive(this.application.getDatabaseManager().getDatabase()); //
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();

        this.application.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Database database) {
        this.database = database;

        Log.i(TAG, "onRefreshLayout");
        onRefreshLayout();
    }

    protected abstract void onRefreshLayout();

    public DoaktivApplication getApplication() {
        return application;
    }

    public RootController getRootController() {
        return getFragmentController();
    }
}
