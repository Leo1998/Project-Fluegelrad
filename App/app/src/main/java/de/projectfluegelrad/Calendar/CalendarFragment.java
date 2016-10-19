package de.projectfluegelrad.Calendar;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalendarFragment extends Fragment {

    private FragmentTabHost tabHost;

    private List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = getArguments().getParcelableArrayList("eventList");

        tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.calendar);

        tabHost.addTab(tabHost.newTabSpec("calendar").setIndicator("Kalender"), CalenderViewFragment.class, getArguments());

        tabHost.addTab(tabHost.newTabSpec("list").setIndicator("Event Liste"), EventListFragment.class, getArguments());

        return tabHost;
    }

}
