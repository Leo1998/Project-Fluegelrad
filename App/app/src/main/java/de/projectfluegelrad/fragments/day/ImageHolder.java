package de.projectfluegelrad.fragments.day;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import de.projectfluegelrad.database.Image;

public class ImageHolder extends RelativeLayout {

    private File imageCacheDir;

    private ImageView imageHolder;
    private ProgressBar progressSpinner;

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

        this.progressSpinner = new ProgressBar(context);
        this.progressSpinner.setId((int) System.currentTimeMillis());
        this.progressSpinner.setIndeterminate(true);

        this.imageHolder = new ImageView(context);
        this.imageHolder.setId((int) System.currentTimeMillis() + 50);
        this.imageHolder.setVisibility(View.INVISIBLE);
        this.imageHolder.setAdjustViewBounds(true);
        this.imageHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);

        final LayoutParams spinnerParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        spinnerParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        final LayoutParams imageHolderParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        imageHolderParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        this.addView(progressSpinner, spinnerParams);
        this.addView(imageHolder, imageHolderParams);
    }

    public void setImage(Image image) {
        if (image != null) {
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

                    ImageHolder.this.progressSpinner.setVisibility(View.INVISIBLE);
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
