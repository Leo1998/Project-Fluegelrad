package de.projectfluegelrad.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import de.projectfluegelrad.R;

public class CircleText extends TextView{
    private Drawable drawable;

    private GradientDrawable pressed;
    private GradientDrawable unpressed;

    public CircleText(Context context) {
        super(context);
        init();
    }

    public CircleText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.touch_selector);
        if (drawable instanceof StateListDrawable){
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.touch_selector);
            DrawableContainer.DrawableContainerState d = ((DrawableContainer.DrawableContainerState) drawable.getConstantState());
            Drawable[] children = d.getChildren();

            Drawable selectedItem = children[0];
            Drawable unselectedItem = children[1];

            pressed = (GradientDrawable) selectedItem;
            unpressed = (GradientDrawable) unselectedItem;

            pressed.setColor(ContextCompat.getColor(getContext(), R.color.shadow));
            unpressed.setColor(Color.TRANSPARENT);
        }
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
        unpressed.setColor(color);
    }
}
