package de.projectfluegelrad.fragments.day;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.Image;

public class ImageHolder extends FrameLayout {

    private File imageCacheDir;

    private ImageView imageHolder;
    private ProgressBar progressBar;

    private Image image;

    public ImageHolder(Context context) {
        super(context);

        init(context);
    }

    public ImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        if (!this.isInEditMode()) {
            this.imageCacheDir = new File(context.getCacheDir(), "imagecache");
            this.imageCacheDir.mkdir();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.image_holder, this);

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.imageHolder = (ImageView) findViewById(R.id.imageView);
    }

    public void setImage(Image image) {
        if (image != null) {
            this.image = image;

            LoadImageTask task = new LoadImageTask();

            task.execute(image);
        }
    }

    private class LoadImageTask extends AsyncTask<Image, Void, Void> {

        protected Void doInBackground(Image... images) {
            assert(images.length == 1);

            final Image image = images[0];

            try {
                image.load(imageCacheDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageHolder.this.post(new Runnable() {
                @Override
                public void run() {
                    ImageHolder.this.imageHolder.setImageBitmap(image.getBitmap());

                    ImageHolder.this.progressBar.setVisibility(View.INVISIBLE);
                    ImageHolder.this.imageHolder.setVisibility(View.VISIBLE);

                    ImageHolder.this.requestLayout();

                }
            });

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }
}
