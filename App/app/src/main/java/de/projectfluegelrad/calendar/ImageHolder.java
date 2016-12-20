package de.projectfluegelrad.calendar;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.projectfluegelrad.database.Image;

public class ImageHolder {

    private ImageView imageHolder;
    private TextView descriptionView;

    private RelativeLayout layout;

    public ImageHolder(Context context, Image image) {
        this.layout = new RelativeLayout(context);

        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        this.imageHolder = new ImageView(context);
        this.imageHolder.setId((int) System.currentTimeMillis());
        this.imageHolder.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.imageHolder.setImageBitmap(image.getBitmap());

        this.descriptionView = new TextView(context);
        this.descriptionView.setId((int) System.currentTimeMillis() + 100);
        this.descriptionView.setGravity(Gravity.CENTER_HORIZONTAL);
        this.descriptionView.setText(image.getDescription());

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);

        RelativeLayout.LayoutParams imageHolderParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout.LayoutParams descriptionViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        descriptionViewParams.addRule(RelativeLayout.BELOW, imageHolder.getId());
        descriptionViewParams.bottomMargin = bottomMargin;

        layout.addView(imageHolder, imageHolderParams);
        layout.addView(descriptionView, descriptionViewParams);
    }

    public RelativeLayout getLayout() {
        return layout;
    }
}
