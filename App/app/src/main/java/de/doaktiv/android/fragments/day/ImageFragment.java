package de.doaktiv.android.fragments.day;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivFragment;
import de.doaktiv.android.fragments.AsyncImageView;
import de.doaktiv.database.Event;
import de.doaktiv.database.Image;

public class ImageFragment extends DoaktivFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_base, container, false);

        Event event = database.getEvent(getArguments().getInt("eventId"));
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
