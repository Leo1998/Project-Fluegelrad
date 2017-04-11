package de.doaktiv.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.fragments.calendar.gridview.CalendarGridViewFragment;
import de.doaktiv.android.fragments.calendar.listview.CalendarListFragment;
import de.doaktiv.android.fragments.home.HomeFragment;
import de.doaktiv.android.fragments.settings.SettingsFragment;
import de.doaktiv.database.DatabaseManager;

public class DoaktivActivity extends AppCompatActivity {

    private static final String TAG = "DoaktivActivity";

    private DoaktivApplication application;
    private Toolbar toolbar;
    private FragmentScreenController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (DoaktivApplication) this.getApplication();//safe cast

        this.setContentView(R.layout.app_bar_main);

        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.doSystemBack();
            }
        });

        this.controller = new FragmentScreenController(this, savedInstanceState);
        this.controller.setListener(new FragmentScreenController.BackStackChangeListener() {
            @Override
            public void onBackStackChanged() {
                if (controller.getBackStackEntryCount() > 1) {
                    toggle.setDrawerIndicatorEnabled(false);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    toggle.setDrawerIndicatorEnabled(true);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_calender) {
                    controller.openScreen(new CalendarGridViewFragment());
                } else if (item.getItemId() == R.id.nav_calender_list) {
                    controller.openScreen(new CalendarListFragment());
                } else if (item.getItemId() == R.id.nav_settings) {
                    controller.openScreen(new SettingsFragment());
                }

                return true;
            }
        });

        getController().openScreen(new HomeFragment());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!controller.doSystemBack()) {
                finish();
            }
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public FragmentScreenController getController() {
        return controller;
    }

    public void showBar() {
        getSupportActionBar().show();
    }

    public void hideBar() {
        getSupportActionBar().hide();
    }

    public DoaktivApplication getDoaktivApplication() {
        return application;
    }

    public DatabaseManager getDatabaseManager() {
        return application.getDatabaseManager();
    }
}
