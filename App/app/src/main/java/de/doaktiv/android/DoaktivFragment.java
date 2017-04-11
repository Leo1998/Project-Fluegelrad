package de.doaktiv.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class DoaktivFragment extends Fragment {

    private DoaktivApplication application;
    private RootController rootController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (DoaktivApplication) getActivity().getApplication();
        this.rootController = ((DoaktivActivity) getActivity()).getController();
    }

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

    public DoaktivActivity getDoaktivActivity(){
        return (DoaktivActivity) getActivity();
    }

    public RootController getRootController() {
        return rootController;
    }
}
