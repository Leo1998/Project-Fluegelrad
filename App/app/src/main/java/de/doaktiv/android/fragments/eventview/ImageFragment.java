package de.doaktiv.android.fragments.eventview;


import android.content.Context;
import android.view.View;

import de.doaktiv.R;
import de.doaktiv.android.base.DoaktivFragment;
import de.doaktiv.android.fragments.AsyncImageView;
import de.doaktiv.database.Database;
import de.doaktiv.database.Event;
import de.doaktiv.database.Image;

public class ImageFragment extends DoaktivFragment {

    @Override
    public View createView(Context context) {
        View rootView = inflater().inflate(R.layout.image_base, null, false);

        Database database = getRootController().getActivity().getDatabase();//bad style
        if (database != null) {
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
        }

        return rootView;
    }

}
