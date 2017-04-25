package de.doaktiv.android.base;

import android.os.Bundle;

import de.doaktiv.android.DoaktivApplication;
import de.doaktiv.android.RootController;

public abstract class DoaktivFragment extends BaseFragment {

    private static final String TAG = "DoaktivFragment";

    private DoaktivApplication application;

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
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    public DoaktivApplication getApplication() {
        return application;
    }

    public RootController getRootController() {
        return getFragmentController();
    }
}
