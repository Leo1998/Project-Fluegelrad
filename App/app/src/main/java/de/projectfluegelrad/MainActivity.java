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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.projectfluegelrad.Calendar.CalendarFragment;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseManager databaseManager;

    private CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

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

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("eventList", (ArrayList<? extends Parcelable>) databaseManager.getEventList());
        //bundle.putParcelableArrayList("eventList", (ArrayList<? extends Parcelable>) events);

        calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarFragment).commit();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_calender) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, calendarFragment).commit();
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_info || id == R.id.nav_info2) {

        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
