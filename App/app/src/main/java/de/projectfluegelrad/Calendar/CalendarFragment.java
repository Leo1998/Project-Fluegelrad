package de.projectfluegelrad.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Event;

public class CalendarFragment extends Fragment {

    private FragmentTabHost tabHost;

    private List<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        events = getArguments().getParcelableArrayList("eventList");

        tabHost = new FragmentTabHost(getActivity());
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.calendar_fragment);


        tabHost.addTab(tabHost.newTabSpec("calendar_fragment").setIndicator("Kalender"), CalenderViewFragment.class, getArguments());
        tabHost.addTab(tabHost.newTabSpec("list").setIndicator("Event Liste"), CalenderListFragment.class, getArguments());

        if (savedInstanceState != null){
            tabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        return tabHost;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", tabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

}
