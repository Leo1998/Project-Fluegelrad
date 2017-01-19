package de.projectfluegelrad.fragments.day;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Image;

public class ImageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_fragment, container, false);

        Image image = DatabaseManager.INSTANCE.getImage(getArguments().getString("imagePath"));

        ImageHolder imageHolder = (ImageHolder) rootView.findViewById(R.id.image);
        imageHolder.setImage(image);

        return rootView;
    }

}
