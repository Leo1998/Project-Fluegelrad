package de.doaktiv.android.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.doaktiv.android.DoaktivActivity;
import de.doaktiv.android.DoaktivApplication;
import de.doaktiv.database.Database;

public class FragmentController {

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
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (currentAnimation != null) {
                return true;
            }

            return super.onInterceptTouchEvent(ev);
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

    private FrameLayout rootContainer;
    private View drawerView;
    private DrawerLayout drawerLayout;
    private LinearLayoutContainer containerViewFront;
    private LinearLayoutContainer containerViewBack;
    private AnimatorSet currentAnimation;
    private int transitionAnimationDuration = 300;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();

    private List<FragmentsStackChangeListener> listeners = new ArrayList<>();
    private List<BaseFragment> fragmentsStack = new ArrayList<>();

    public FragmentController(DoaktivActivity activity, FrameLayout rootContainer, Bundle savedState) {//TODO: save state
        this.activity = activity;
        this.application = activity.getDoaktivApplication();

        init(rootContainer);
    }

    private void init(FrameLayout rootContainer) {
        this.rootContainer = rootContainer;

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.LEFT);

        this.drawerLayout = new DrawerLayout(activity);
        drawerLayout.setLayoutParams(layoutParams);
        rootContainer.addView(drawerLayout);

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
            if (activity.getDatabase() != null) {
                fragment.onDatabaseReceived(activity.getDatabase());
            }

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
