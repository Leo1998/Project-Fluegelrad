package de.doaktiv.android.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.android.DoaktivApplication;
import de.doaktiv.database.Database;
import de.doaktiv.util.AndroidUtils;

public class FragmentController extends FrameLayout {

    private static final String TAG = "FragmentController";

    public class LinearLayoutContainer extends LinearLayout {

        public LinearLayoutContainer(Context context) {
            super(context);
            setOrientation(VERTICAL);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            return super.drawChild(canvas, child, drawingTime);
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public interface FragmentsStackChangeListener {
        void onFragmentsStackChanged();
    }

    private DoaktivActivity activity;
    private DoaktivApplication application;

    private View drawerView;
    private DrawerLayout drawerLayout;
    private LinearLayoutContainer containerViewFront;
    private LinearLayoutContainer containerViewBack;
    private AnimatorSet currentAnimation;
    private int transitionAnimationDuration = 300;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private boolean startedTracking = false;
    private boolean maybeStartTracking = false;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker;
    private float innerTranslationX;

    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;

    private List<FragmentsStackChangeListener> listeners = new ArrayList<>();
    private List<BaseFragment> fragmentsStack = new ArrayList<>();

    public FragmentController(DoaktivActivity activity, Bundle savedState) {//TODO: save state
        super(activity);

        this.activity = activity;
        this.application = activity.getDoaktivApplication();

        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(R.drawable.layer_shadow);
            scrimPaint = new Paint();
        }

        init();
    }

    private void init() {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.LEFT);

        this.drawerLayout = new DrawerLayout(activity);
        drawerLayout.setLayoutParams(layoutParams);
        addView(drawerLayout);

        this.containerViewBack = new LinearLayoutContainer(activity);
        containerViewBack.setLayoutParams(layoutParams);
        containerViewBack.setVisibility(View.GONE);
        drawerLayout.addView(containerViewBack);
        this.containerViewFront = new LinearLayoutContainer(activity);
        containerViewFront.setLayoutParams(layoutParams);
        drawerLayout.addView(containerViewFront);
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public View getDrawerView() {
        return drawerView;
    }

    public void setDrawerView(View drawerView) {
        if (this.drawerView != null)
            return;

        this.drawerView = drawerView;
        this.drawerView.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.START));
        drawerLayout.addView(this.drawerView);

        this.addListener(new FragmentsStackChangeListener() {
            @Override
            public void onFragmentsStackChanged() {
                if (fragmentsStack.size() == 1) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    Toolbar toolbar = fragmentsStack.get(fragmentsStack.size() - 1).getToolbar();
                    if (toolbar != null) {
                        toolbar.setNavigationButtonState(Toolbar.NavigationButtonState.Back, false);
                        toolbar.setNavigationButtonState(Toolbar.NavigationButtonState.Menu);
                    }
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        });
    }

    public List<FragmentsStackChangeListener> getListeners() {
        return listeners;
    }

    public void addListener(FragmentsStackChangeListener listener) {
        this.listeners.add(listener);
    }

    public DoaktivActivity getActivity() {
        return activity;
    }

    public DoaktivApplication getApplication() {
        return application;
    }

    public List<BaseFragment> getFragmentsStack() {
        return fragmentsStack;
    }

    public int getBackStackEntryCount() {
        return fragmentsStack.size();
    }

    public void onResume() {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onResume();
        }
    }

    public void onPause() {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onPause();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onConfigurationChanged(newConfig);
        }
    }

    public void onBackPressed() {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            if (!lastFragment.onBackPressed()) {
                activity.finish();
            }
        }
    }

    public void onDatabaseReceived(Database database) {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onDatabaseReceived(database);
        }
    }

    private void attachFragmentToContainer(BaseFragment fragment, LinearLayoutContainer container) {
        container.setBackgroundColor(fragment.getBackgroundColor());

        View fragmentView = fragment.getFragmentView();
        if (fragmentView == null) {
            fragmentView = fragment.createView(activity);
            fragment.setFragmentView(fragmentView);
        } else {
            detachFragmentFromContainer(fragment);
        }

        if (fragment.getToolbar() != null && fragment.isAddToolbarToContainer()) {
            container.addView(fragment.getToolbar());
        }

        if (fragmentView != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            fragmentView.setLayoutParams(layoutParams);
            container.addView(fragmentView);
        }
    }

    private void detachFragmentFromContainer(BaseFragment fragment) {
        if (fragment.getFragmentView() != null) {
            ViewGroup parent = (ViewGroup) fragment.getFragmentView().getParent();
            if (parent != null) {
                parent.removeView(fragment.getFragmentView());
            }
        }
        if (fragment.getToolbar() != null && fragment.isAddToolbarToContainer()) {
            ViewGroup parent = (ViewGroup) fragment.getToolbar().getParent();
            if (parent != null) {
                parent.removeView(fragment.getToolbar());
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int clipLeft = getPaddingLeft();
        int clipRight = width + getPaddingLeft();
        int translationX = (int) innerTranslationX + getPaddingRight();

        if (child == containerViewBack) {
            clipRight = translationX;
        } else if (child == containerViewFront) {
            clipLeft = translationX;
        }

        final int restoreCount = canvas.save();
        if (currentAnimation == null) {
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        final boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        /*if (translationX != 0) {
            if (child == containerViewFront) {
                final float alpha = Math.max(0, Math.min((width - translationX) / (float) AndroidUtils.dp(20), 1.0f));
                layerShadowDrawable.setBounds(translationX - layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (0xff * alpha));
                layerShadowDrawable.draw(canvas);
            } else if (child == containerViewBack) {
                float opacity = Math.min(0.8f, (width - translationX) / (float) width);
                if (opacity < 0) {
                    opacity = 0;
                }
                scrimPaint.setColor((int) (((0x99000000 & 0xff000000) >>> 24) * opacity) << 24);
                canvas.drawRect(clipLeft, 0, clipRight, getHeight(), scrimPaint);
            }
        }*/

        return result;
    }

    public float getInnerTranslationX() {
        return innerTranslationX;
    }

    public void setInnerTranslationX(float value) {
        innerTranslationX = value;
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return currentAnimation != null || onTouchEvent(ev);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);

        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (currentAnimation == null) {
            if (fragmentsStack.size() > 1) {
                if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN && !startedTracking && !maybeStartTracking) {
                    BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                    if (!currentFragment.isSwipeBackEnabled()) {
                        return false;
                    }

                    maybeStartTracking = true;
                    startedTrackingPointerId = ev.getPointerId(0);
                    startedTrackingX = (int) ev.getX();
                    startedTrackingY = (int) ev.getY();
                    if (velocityTracker != null) {
                        velocityTracker.clear();
                    }
                } else if (ev != null && ev.getAction() == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    }
                    int dx = Math.max(0, (int) (ev.getX() - startedTrackingX));
                    int dy = Math.abs((int) ev.getY() - startedTrackingY);
                    velocityTracker.addMovement(ev);
                    if (maybeStartTracking && !startedTracking && dx >= AndroidUtils.dp(6) && Math.abs(dx) / 3 > dy) {
                        prepareForMoving(ev);
                    } else if (startedTracking) {
                        /*if (!beginTrackingSent) {
                            if (parentActivity.getCurrentFocus() != null) {
                                AndroidUtilities.hideKeyboard(parentActivity.getCurrentFocus());
                            }
                            BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                            currentFragment.onBeginSlide();
                            beginTrackingSent = true;
                        }*/
                        containerViewFront.setTranslationX(dx);
                        setInnerTranslationX(dx);
                    }
                } else if (ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_POINTER_UP)) {
                    if (velocityTracker == null) {
                        velocityTracker = VelocityTracker.obtain();
                    }
                    velocityTracker.computeCurrentVelocity(1000);
                    if (!startedTracking && fragmentsStack.get(fragmentsStack.size() - 1).isSwipeBackEnabled()) {
                        float velX = velocityTracker.getXVelocity();
                        float velY = velocityTracker.getYVelocity();
                        if (velX >= 3500 && velX > Math.abs(velY)) {
                            prepareForMoving(ev);
                            /*if (!beginTrackingSent) {
                                if (((Activity) getContext()).getCurrentFocus() != null) {
                                    AndroidUtilities.hideKeyboard(((Activity) getContext()).getCurrentFocus());
                                }
                                beginTrackingSent = true;
                            }*/
                        }
                    }
                    if (startedTracking) {
                        float x = containerViewFront.getX();
                        currentAnimation = new AnimatorSet();
                        float velX = velocityTracker.getXVelocity();
                        float velY = velocityTracker.getYVelocity();
                        final boolean backAnimation = x < containerViewFront.getMeasuredWidth() / 3.0f && (velX < 3500 || velX < velY);
                        float distToMove;
                        if (!backAnimation) {
                            distToMove = containerViewFront.getMeasuredWidth() - x;
                            currentAnimation.playTogether(
                                    ObjectAnimator.ofFloat(containerViewFront, "translationX", containerViewFront.getMeasuredWidth()),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", (float) containerViewFront.getMeasuredWidth())
                            );
                        } else {
                            distToMove = x;
                            currentAnimation.playTogether(
                                    ObjectAnimator.ofFloat(containerViewFront, "translationX", 0),
                                    ObjectAnimator.ofFloat(this, "innerTranslationX", 0)
                            );
                        }

                        currentAnimation.setDuration(Math.max((int) ((float) transitionAnimationDuration / containerViewFront.getMeasuredWidth() * distToMove), 50));
                        currentAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                onSlideAnimationEnd(backAnimation);
                            }
                        });
                        currentAnimation.start();
                    } else {
                        maybeStartTracking = false;
                        startedTracking = false;
                    }
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                } else if (ev == null) {
                    maybeStartTracking = false;
                    startedTracking = false;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        velocityTracker = null;
                    }
                }

                return startedTracking;
            }
        }

        return false;
    }

    private void prepareForMoving(MotionEvent ev) {
        maybeStartTracking = false;
        startedTracking = true;
        startedTrackingX = (int) ev.getX();

        BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        lastFragment.onResume();

        containerViewBack.setVisibility(View.VISIBLE);
        attachFragmentToContainer(lastFragment, containerViewBack);
    }

    private void onSlideAnimationEnd(final boolean backAnimation) {
        if (!backAnimation) {
            BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            currentFragment.onPause();
            currentFragment.onFragmentDestroy();//TODO
            currentFragment.setFragmentController(null);
            fragmentsStack.remove(currentFragment);
            for (FragmentsStackChangeListener listener : listeners)
                listener.onFragmentsStackChanged();

            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);

            detachFragmentFromContainer(currentFragment);
            detachFragmentFromContainer(lastFragment);
            attachFragmentToContainer(lastFragment, containerViewFront);
        } else {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
            lastFragment.onPause();

            detachFragmentFromContainer(lastFragment);
        }

        onTransitionAnimationEnd();
        startedTracking = false;
    }

    private void checkTransitionAnimation() {
        if (currentAnimation != null) {
            currentAnimation.cancel();
            currentAnimation = null;
        }
    }

    private void onTransitionAnimationEnd() {
        containerViewBack.setVisibility(View.GONE);

        containerViewFront.setAlpha(1.0f);
        containerViewFront.setScaleX(1.0f);
        containerViewFront.setScaleY(1.0f);
        containerViewFront.setTranslationX(0.0f);
        containerViewBack.setAlpha(1.0f);
        containerViewBack.setScaleX(1.0f);
        containerViewBack.setScaleY(1.0f);
        containerViewBack.setTranslationX(0.0f);

        setInnerTranslationX(0);

        currentAnimation = null;
    }

    public void presentFragment(BaseFragment fragment) {
        presentFragment(fragment, true);
    }

    public void presentFragment(BaseFragment fragment, boolean animate) {
        checkTransitionAnimation();

        final BaseFragment currentFragment = !fragmentsStack.isEmpty() ? fragmentsStack.get(fragmentsStack.size() - 1) : null;
        if (currentFragment != null) {
            currentFragment.onPause();
        }

        fragment.setFragmentController(this);
        fragment.onFragmentCreate();
        fragment.onResume();

        fragmentsStack.add(fragment);
        for (FragmentsStackChangeListener listener : listeners)
            listener.onFragmentsStackChanged();

        if (animate && currentFragment != null) {
            containerViewBack.setVisibility(View.VISIBLE);
            detachFragmentFromContainer(currentFragment);
            attachFragmentToContainer(currentFragment, containerViewBack);
            attachFragmentToContainer(fragment, containerViewFront);

            List<Animator> animators = fragment.getEnterAnimators(containerViewFront);

            currentAnimation = new AnimatorSet();
            currentAnimation.playTogether(animators);
            currentAnimation.setInterpolator(decelerateInterpolator);
            currentAnimation.setDuration(transitionAnimationDuration);
            currentAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    detachFragmentFromContainer(currentFragment);

                    onTransitionAnimationEnd();
                }
            });
            currentAnimation.start();
        } else {
            if (currentFragment != null) {
                detachFragmentFromContainer(currentFragment);
            }
            attachFragmentToContainer(fragment, containerViewFront);
        }

        //TODO: bad style
        if (activity.getDatabase() != null) {
            fragment.onDatabaseReceived(activity.getDatabase());
        }
    }

    public void popFragment() {
        popFragment(true);
    }

    public void popFragment(boolean animate) {
        if (fragmentsStack.size() > 1) {
            checkTransitionAnimation();

            final BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            final BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);

            final Runnable afterAnimationRunnable = new Runnable() {
                @Override
                public void run() {
                    currentFragment.onPause();
                    currentFragment.onFragmentDestroy();//TODO
                    currentFragment.setFragmentController(null);
                    fragmentsStack.remove(currentFragment);
                    for (FragmentsStackChangeListener listener : listeners)
                        listener.onFragmentsStackChanged();

                    lastFragment.onResume();
                }
            };

            if (animate) {
                containerViewBack.setVisibility(View.VISIBLE);
                attachFragmentToContainer(lastFragment, containerViewBack);

                List<Animator> animators = currentFragment.getExitAnimators(containerViewFront);

                currentAnimation = new AnimatorSet();
                currentAnimation.playTogether(animators);
                currentAnimation.setInterpolator(decelerateInterpolator);
                currentAnimation.setDuration(transitionAnimationDuration);
                currentAnimation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        afterAnimationRunnable.run();

                        detachFragmentFromContainer(currentFragment);
                        detachFragmentFromContainer(lastFragment);
                        attachFragmentToContainer(lastFragment, containerViewFront);

                        onTransitionAnimationEnd();
                    }
                });
                currentAnimation.start();
            } else {
                afterAnimationRunnable.run();

                detachFragmentFromContainer(currentFragment);
                attachFragmentToContainer(lastFragment, containerViewFront);
            }
        }
    }

    public boolean doSystemBack() {
        if (fragmentsStack.size() > 1) {
            popFragment();

            return true;
        } else {
            return false;
        }
    }
}
