package de.doaktiv.android.fragments.eventview;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.doaktiv.R;
import de.doaktiv.android.fragments.AsyncImageView;
import de.doaktiv.database.Event;

public class ImageSliderAdapter extends PagerAdapter {

    private Event event;

    private LayoutInflater inflater;

    public ImageSliderAdapter(Context context, Event event) {
        this.event = event;

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.image_base, container, false);

        AsyncImageView imageView = (AsyncImageView) view.findViewById(R.id.image);
        imageView.setImageAsync(event.getImages().get(position));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getCount() {
        return event.getImages().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return event.getImages().get(position).getDescription();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
