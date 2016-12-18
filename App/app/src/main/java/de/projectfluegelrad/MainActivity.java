package de.projectfluegelrad;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        toggle.setToolbarNavigationClickListener(v -> {
            getSupportFragmentManager().popBackStack();
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                toggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                toggle.setDrawerIndicatorEnabled(true);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.databaseManager = new DatabaseManager(navigationView, new File(getApplicationContext().getFilesDir(), "database"));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("eventList", (ArrayList<? extends Parcelable>) databaseManager.getEventList());
        bundle.putParcelable("imageAtlas", databaseManager.getImageAtlas());

        calendarFragment = new CalendarGridViewFragment();
        calendarFragment.setArguments(bundle);

        calendarListFragment = new CalendarListFragment();
        calendarListFragment.setArguments(bundle);

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
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        } else if (id == R.id.nav_calender) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarFragment).commit();
        } else if (id == R.id.nav_calender_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarListFragment).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        }
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
