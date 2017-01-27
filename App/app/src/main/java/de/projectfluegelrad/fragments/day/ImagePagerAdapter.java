package de.projectfluegelrad.fragments.day;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.List;

import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;

public class ImagePagerAdapter extends FragmentStatePagerAdapter{

    private Event event;

    public ImagePagerAdapter(FragmentManager fragmentManager, Event event) {
        super(fragmentManager);

        this.event = event;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", event.getId());
        bundle.putString("imagePath", event.getImages().get(i).getPath());

        Fragment fragment = new ImageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return event.getImages().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return event.getImages().get(position).getDescription();
    }

}
