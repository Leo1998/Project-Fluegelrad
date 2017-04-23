package de.doaktiv.android;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import de.doaktiv.R;
import de.doaktiv.android.base.FragmentController;
import de.doaktiv.database.DatabaseManager;

public class DoaktivActivity extends Activity {

    private static final String TAG = "DoaktivActivity";

    private DoaktivApplication application;
    private FragmentController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (DoaktivApplication) this.getApplication();//safe cast

        this.setContentView(R.layout.app_main);
        this.controller = new FragmentController(this, (FrameLayout) this.findViewById(android.R.id.content), savedInstanceState);

        NavigationView navigationView = new NavigationView(this);
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_calender) {
                    controller.openCalendar();
                } else if (item.getItemId() == R.id.nav_calender_list) {
                    controller.openEventList();
                } else if (item.getItemId() == R.id.nav_settings) {
                    controller.openSettings();
                }

                return true;
            }
        });
        controller.setDrawerView(navigationView);

        getController().openHome();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        controller.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        controller.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        controller.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        controller.onBackPressed();
    }

    public FragmentController getController() {
        return controller;
    }

    public DoaktivApplication getDoaktivApplication() {
        return application;
    }

    public DatabaseManager getDatabaseManager() {
        return application.getDatabaseManager();
    }
}
