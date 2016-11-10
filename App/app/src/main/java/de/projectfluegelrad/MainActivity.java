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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.projectfluegelrad.calendar.CalendarFragment;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.DatabaseRequest;
import de.projectfluegelrad.database.Event;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseManager databaseManager;

    private CalendarFragment calendarFragment;
    private HomeFragment homeFragment;
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
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        /*List<Event> events = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 50; i++){
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, r.nextInt(100) - 50);
            Event e = new Event(i, "Strasse" + r.nextInt(100), "Katerogie" + r.nextInt(100), r.nextInt(10), "Verein" + r.nextInt(100), new Date(c.getTimeInMillis()), "Beschreibung" + r.nextInt(100));
            events.add(e);
        }*/

        List<Event> events = databaseManager.getEventList();

        Collections.sort(events, (event, t1) -> {
            if (event.getDate() == null || t1.getDate() == null) {
                return 0;
            }

            return event.getDate().compareTo(t1.getDate());
        });

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("eventList", (ArrayList<? extends Parcelable>) events);

        calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(bundle);

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

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        } else if (id == R.id.nav_calender) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarFragment).commit();
        } else if (id == R.id.nav_search) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        } else if (id == R.id.nav_calender) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarFragment).commit();
        } else if (id == R.id.nav_search) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchFragment).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingsFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
