package de.projectfluegelrad.fragments.day;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.DatabaseManager;
import de.projectfluegelrad.database.Event;
import de.projectfluegelrad.database.Image;
import de.projectfluegelrad.fragments.AsyncImageView;

public class ImageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_base, container, false);

        Event event = DatabaseManager.INSTANCE.getEvent(getArguments().getInt("eventId"));
        Image image = null;
        for (Image i : event.getImages()) {
            if (i.getPath().equals(getArguments().getString("imagePath"))) {
                image = i;
            }
        }

        if (image != null) {
            AsyncImageView imageView = (AsyncImageView) rootView.findViewById(R.id.image);
            imageView.setImageAsync(image);
        }

        return rootView;
    }

}
