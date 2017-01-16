package de.projectfluegelrad.fragments.day;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;

public final class FixFlingBehavior extends AppBarLayout.Behavior {

    private boolean isPositive;

    public FixFlingBehavior() {
    }

    public FixFlingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
            velocityY *= -1;
        }

        if (target instanceof NestedScrollView && Math.abs(velocityY) > 800) {
            consumed = false;
        }
        if (target instanceof NestedScrollingChild && Math.abs(velocityY) > 800) {
            consumed = false;
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        isPositive = dy > 0;
    }

}