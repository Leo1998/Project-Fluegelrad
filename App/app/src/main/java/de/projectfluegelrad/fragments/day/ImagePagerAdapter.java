package de.projectfluegelrad.fragments.day;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import de.projectfluegelrad.database.Image;

public class ImagePagerAdapter extends FragmentStatePagerAdapter{

    private List<Image> images;

    public ImagePagerAdapter(FragmentManager fragmentManager, List<Image> images) {
        super(fragmentManager);

        this.images = images;
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", images.get(i).getPath());

        Fragment fragment = new ImageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return images.get(position).getDescription();
    }

}
