package de.doaktiv.android.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import de.doaktiv.database.Image;
import de.doaktiv.util.AndroidUtils;

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

    public void setImageAsync(Image image) {
        this.image = image;

        if (image != null) {
            LoadImageTask task = new LoadImageTask();

            task.execute(image);
        }
    }

    private class LoadImageTask extends AsyncTask<Image, Void, Void> {

        @Override
        protected Void doInBackground(Image... images) {
            assert (images.length == 1);

            final Image image = images[0];

            if (AndroidUtils.isNetworkConnected()) {
                try {
                    image.load(imageCacheDir);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                AsyncImageView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        AsyncImageView.this.setImageBitmap(image.getBitmap());
                        AsyncImageView.this.requestLayout();
                        AsyncImageView.this.setContentDescription(image.getDescription());
                    }
                });
            }

            return null;
        }
    }

}
