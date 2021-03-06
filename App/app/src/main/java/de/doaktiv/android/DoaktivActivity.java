package de.doaktiv.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;

import de.doaktiv.R;
import de.doaktiv.android.base.FragmentController;
import de.doaktiv.android.fragments.SponsorFragment;
import de.doaktiv.android.fragments.calendar.CalendarFragment;
import de.doaktiv.android.fragments.eventlist.EventListFragment;
import de.doaktiv.android.fragments.eventview.EventViewFragment;
import de.doaktiv.android.fragments.eventview.ParticipateFragment;
import de.doaktiv.android.fragments.home.HomeFragment;
import de.doaktiv.android.fragments.settings.SettingsFragment;
import de.doaktiv.database.Database;

public class DoaktivActivity extends Activity implements RootController {

    private static final String TAG = "DoaktivActivity";

    private final BroadcastReceiver databaseUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Database database = getDatabase();

            if (database != null) {
                controller.onDatabaseReceived(database);
            }
        }
    };

    private final ServiceConnection databaseServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
            databaseService = binder.getService();
            Log.i(TAG, "DatabaseService connected!");

            // as database is now available send update broadcast
            Intent intent = new Intent("de.doaktiv.databaseUpdate");
            sendBroadcast(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "DatabaseService disconnected!");
        }
    };

    private DoaktivApplication application;
    private FragmentController controller;
    private DatabaseService databaseService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (DoaktivApplication) this.getApplication();//safe cast

        this.controller = new FragmentController(this, savedInstanceState);
        this.setContentView(controller);

        NavigationView navigationView = new NavigationView(this);
        navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_calender) {
                    openCalendar();
                } else if (item.getItemId() == R.id.nav_calender_list) {
                    openEventList();
                } else if (item.getItemId() == R.id.nav_settings) {
                    openSettings();
                }

                return true;
            }
        });
        controller.setDrawerView(navigationView);

        openHome();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, databaseServiceConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(databaseUpdateReceiver, new IntentFilter("de.doaktiv.databaseUpdate"));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (databaseService != null) {
            unbindService(databaseServiceConnection);
        }

        unregisterReceiver(databaseUpdateReceiver);
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

    public Database getDatabase() {
        if (databaseService != null) {
            return databaseService.getDatabase();
        }

        return null;
    }

    @Override
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public boolean doSystemBack() {
        return controller.doSystemBack();
    }

    @Override
    public void openHome() {
        controller.presentFragment(new HomeFragment());
    }

    @Override
    public void openCalendar() {
        controller.presentFragment(new CalendarFragment());
    }

    @Override
    public void openEventList() {
        controller.presentFragment(new EventListFragment());
    }

    @Override
    public void openSettings() {
        controller.presentFragment(new SettingsFragment());
    }

    @Override
    public void openEventView(int eventId) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);

        EventViewFragment fragment = new EventViewFragment();
        fragment.setArguments(bundle);

        controller.presentFragment(fragment);
    }

    @Override
    public void openParticipateView(int eventId) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);

        ParticipateFragment fragment = new ParticipateFragment();
        fragment.setArguments(bundle);

        controller.presentFragment(fragment);
    }

    @Override
    public void openSponsorView(int sponsorId) {
        Bundle bundle = new Bundle();
        bundle.putInt("sponsorId", sponsorId);

        SponsorFragment fragment = new SponsorFragment();
        fragment.setArguments(bundle);

        controller.presentFragment(fragment);
    }
}
