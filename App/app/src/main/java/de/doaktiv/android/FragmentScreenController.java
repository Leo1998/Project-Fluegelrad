package de.doaktiv.android;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.ArrayList;

import de.doaktiv.R;
import de.doaktiv.android.fragments.calendar.SponsorFragment;
import de.doaktiv.android.fragments.calendar.gridview.CalendarGridViewFragment;
import de.doaktiv.android.fragments.calendar.listview.CalendarListFragment;
import de.doaktiv.android.fragments.eventview.EventViewFragment;
import de.doaktiv.android.fragments.eventview.ParticipateFragment;
import de.doaktiv.android.fragments.home.HomeFragment;
import de.doaktiv.android.fragments.settings.SettingsFragment;

public class FragmentScreenController implements RootController {

    public interface BackStackChangeListener {
        void onBackStackChanged();
    }

    private DoaktivActivity activity;
    private DoaktivApplication application;
    private View container;

    private BackStackChangeListener listener;
    private ArrayList<DoaktivFragment> backStack = new ArrayList<DoaktivFragment>();

    public FragmentScreenController(DoaktivActivity activity, Bundle savedState) {//TODO: save state
        this.activity = activity;
        this.application = activity.getDoaktivApplication();
        this.container = activity.findViewById(R.id.fragment_container);
    }

    public ArrayList<DoaktivFragment> getBackStack() {
        return backStack;
    }

    public int getBackStackEntryCount() {
        return backStack.size();
    }

    private FragmentTransaction prepareTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_open_enter, R.anim.fragment_open_exit);
        } else {
            return activity.getSupportFragmentManager().beginTransaction();
        }
    }

    private FragmentTransaction prepareBackTransaction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_close_enter, R.anim.fragment_close_exit);
        } else {
            return activity.getSupportFragmentManager().beginTransaction();
        }
    }

    private void openScreen(DoaktivFragment fragment) {
        FragmentTransaction transaction = prepareTransaction();

        if (backStack.size() > 0) {
            DoaktivFragment backFragment = backStack.get(backStack.size() - 1);
            transaction.detach(backFragment);
            transaction.add(R.id.fragment_container, fragment, "backstack#" + backStack.size());
        } else {
            transaction.replace(R.id.fragment_container, fragment, "backstack#" + backStack.size());
        }

        transaction.commit();

        backStack.add(fragment);
        if (listener != null)
            listener.onBackStackChanged();
    }

    public void popFragment() {
        if (backStack.size() > 1) {
            DoaktivFragment currentFragment = backStack.get(backStack.size() - 1);
            DoaktivFragment prevFragment = backStack.get(backStack.size() - 2);

            backStack.remove(backStack.size() - 1);
            if (listener != null)
                listener.onBackStackChanged();

            prepareBackTransaction()
                    .remove(currentFragment)
                    .attach(prevFragment)
                    .commit();
        }
    }

    @Override
    public boolean doSystemBack() {
        if (backStack.size() > 1) {
            popFragment();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void openHome() {
        openScreen(new HomeFragment());
    }

    @Override
    public void openCalendar() {
        openScreen(new CalendarGridViewFragment());
    }

    @Override
    public void openEventList() {
        openScreen(new CalendarListFragment());
    }

    @Override
    public void openSettings() {
        openScreen(new SettingsFragment());
    }

    @Override
    public void openEventView(int eventId) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);

        EventViewFragment fragment = new EventViewFragment();
        fragment.setArguments(bundle);

        openScreen(fragment);
    }

    @Override
    public void openParticipateView(int eventId) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);

        ParticipateFragment fragment = new ParticipateFragment();
        fragment.setArguments(bundle);

        openScreen(fragment);
    }

    @Override
    public void openSponsorView(int sponsorId) {
        Bundle bundle = new Bundle();
        bundle.putInt("sponsorId", sponsorId);

        SponsorFragment fragment = new SponsorFragment();
        fragment.setArguments(bundle);

        openScreen(fragment);
    }

    public BackStackChangeListener getListener() {
        return listener;
    }

    public void setListener(BackStackChangeListener listener) {
        this.listener = listener;
    }
}
