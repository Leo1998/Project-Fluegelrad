package de.projectfluegelrad.fragments.day;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Image;

@SuppressLint("ValidFragment")
public class ImageFragment extends Fragment {

    private Image image;

    @SuppressLint("ValidFragment")
    public ImageFragment(Image image) {
        this.image = image;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_fragment, container, false);

        ImageHolder imageHolder = (ImageHolder) rootView.findViewById(R.id.image);
        imageHolder.setImage(this.image);

        return rootView;
    }

}
