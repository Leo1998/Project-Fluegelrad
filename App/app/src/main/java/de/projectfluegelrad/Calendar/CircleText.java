package de.projectfluegelrad.Calendar;

import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.TextView;

import de.projectfluegelrad.R;

public class CircleText extends TextView{
    private Drawable drawable;

    public CircleText(Context context) {
        super(context);
        drawable = ContextCompat.getDrawable(context, R.drawable.touch_selector);
    }

    public CircleText(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawable = ContextCompat.getDrawable(context, R.drawable.touch_selector);
    }

    public CircleText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        drawable = ContextCompat.getDrawable(context, R.drawable.touch_selector);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawable.setBounds(0, 0, getWidth(), getWidth());
        drawable.draw(canvas);

        super.onDraw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        if (drawable != null){
            drawable.setState(getDrawableState());
            invalidate();
        }

        super.drawableStateChanged();

    }


    @Override
    public void setBackgroundColor(int color) {
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.touch_selector_event);
    }
}
