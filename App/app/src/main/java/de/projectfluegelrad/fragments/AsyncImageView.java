package de.projectfluegelrad.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import de.projectfluegelrad.database.Image;

public class AsyncImageView extends ImageView {

    private File imageCacheDir;

    private Image image;

    public AsyncImageView(Context context) {
        super(context);
        init(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            this.imageCacheDir = new File(context.getCacheDir(), "imagecache");
            this.imageCacheDir.mkdir();
        }

        this.setAdjustViewBounds(true);
        this.setScaleType(ScaleType.CENTER_CROP);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);

        this.setImageBitmap(this.image.getBitmap());
    }

    public void setImageAsync(Image image) {
        if (image != null) {
            this.image = image;
            LoadImageTask task = new LoadImageTask();

            task.execute(image);
        }
    }

    private class LoadImageTask extends AsyncTask<Image, Void, Void> {

        @Override
        protected Void doInBackground(Image... images) {
            assert(images.length == 1);

            final Image image = images[0];

            try {
                image.load(imageCacheDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            AsyncImageView.this.post(new Runnable() {
                @Override
                public void run() {
                    AsyncImageView.this.setImageBitmap(image.getBitmap());
                }
            });

            return null;
        }
    }

}
