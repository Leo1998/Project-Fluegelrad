package de.projectfluegelrad;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.calendar.gridview.CalendarGridViewFragment;
import de.projectfluegelrad.calendar.listview.CalendarListFragment;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.DatabaseRequest;
import de.projectfluegelrad.database.Event;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseManager databaseManager;

    private HomeFragment homeFragment;
    private CalendarListFragment calendarListFragment;
    private CalendarGridViewFragment calendarFragment;
    private SettingsFragment settingsFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.getSupportFragmentManager().popBackStack();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (MainActivity.this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    MainActivity.this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    MainActivity.this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.databaseManager = new DatabaseManager(navigationView, new File(getApplicationContext().getFilesDir(), "database"));

        calendarFragment = new CalendarGridViewFragment();

        calendarListFragment = new CalendarListFragment();

        homeFragment = new HomeFragment();
        settingsFragment = new SettingsFragment();
        searchFragment = new SearchFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        onFragmentTransaction(id);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }

        int id = item.getItemId();

        onFragmentTransaction(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Helper method
     *
     * @param id
     */
    private void onFragmentTransaction(int id) {
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = homeFragment;
        } else if (id == R.id.nav_calender) {
            fragment = calendarFragment;
        } else if (id == R.id.nav_calender_list) {
            fragment = calendarListFragment;
        } else if (id == R.id.nav_settings) {
            fragment = settingsFragment;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        databaseManager.request(DatabaseRequest.SaveEventList, false);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
