package de.projectfluegelrad.fragments.calendar;

import android.content.Context;
import android.os.AsyncTask;
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

public class ImageHolder {

    private File cacheDir;

    private ImageView imageHolder;
    private TextView descriptionView;
    private ProgressBar progressSpinner;

    private RelativeLayout layout;

    public ImageHolder(Context context, Image image) {
        this.cacheDir = context.getCacheDir();

        this.layout = new RelativeLayout(context);

        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        this.progressSpinner = new ProgressBar(context);
        this.progressSpinner.setId((int) System.currentTimeMillis());
        this.progressSpinner.setIndeterminate(true);

        this.imageHolder = new ImageView(context);
        this.imageHolder.setId((int) System.currentTimeMillis() + 50);
        this.imageHolder.setVisibility(View.INVISIBLE);
        this.imageHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);

        this.descriptionView = new TextView(context);
        this.descriptionView.setId((int) System.currentTimeMillis() + 100);
        this.descriptionView.setVisibility(View.INVISIBLE);
        this.descriptionView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.descriptionView.setText(image.getDescription());

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);

        RelativeLayout.LayoutParams spinnerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        final RelativeLayout.LayoutParams imageHolderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams descriptionViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        descriptionViewParams.addRule(RelativeLayout.BELOW, imageHolder.getId());
        descriptionViewParams.bottomMargin = bottomMargin;

        layout.addView(progressSpinner, spinnerParams);
        layout.addView(imageHolder, imageHolderParams);
        layout.addView(descriptionView, descriptionViewParams);

        LoadImageTask task = new LoadImageTask();

        task.execute(image);
    }

    private class LoadImageTask extends AsyncTask<Image, Void, Void> {

        protected Void doInBackground(Image... images) {
            assert(images.length == 1);

            final Image image = images[0];

            try {
                image.load(cacheDir);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageHolder.this.layout.post(new Runnable() {
                @Override
                public void run() {
                    ImageHolder.this.imageHolder.setImageBitmap(image.getBitmap());

                    ImageHolder.this.progressSpinner.setVisibility(View.INVISIBLE);
                    ImageHolder.this.imageHolder.setVisibility(View.VISIBLE);
                    ImageHolder.this.descriptionView.setVisibility(View.VISIBLE);

                    layout.requestLayout();

                }
            });

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Long result) {
        }
    }

    public RelativeLayout getLayout() {
        return layout;
    }
}
