package de.doaktiv.android.base;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.database.Database;
import de.doaktiv.util.AndroidUtils;

public abstract class BaseFragment {

    private static final String TAG = "BaseFragment";

    private View fragmentView;
    private int backgroundColor = Color.WHITE;
    private Toolbar toolbar;
    private boolean addToolbarToContainer = true;
    private Bundle arguments;
    private FragmentController fragmentController;

    public BaseFragment() {
    }

    public BaseFragment(Bundle args) {
        this.arguments = args;
    }

    public FragmentController getFragmentController() {
        return fragmentController;
    }

    public void setFragmentController(FragmentController fragmentController) {
        this.fragmentController = fragmentController;

        if (fragmentController != null) {
            this.createToolbar(fragmentController.getActivity());
        }
    }

    public View getFragmentView() {
        return fragmentView;
    }

    void setFragmentView(View fragmentView) {
        this.fragmentView = fragmentView;
    }

    public View createView(Context context) {
        return null;
    }

    public boolean isAddToolbarToContainer() {
        return addToolbarToContainer;
    }

    public void setAddToolbarToContainer(boolean addToolbarToContainer) {
        this.addToolbarToContainer = addToolbarToContainer;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    protected void createToolbar(Context context) {
        this.toolbar = new Toolbar(context);
        toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));

        toolbar.addListener(new Toolbar.ToolbarListener() {
            @Override
            public void onBackPressed() {
                if (toolbar.getNavigationButtonState() == Toolbar.NavigationButtonState.Back) {
                    fragmentController.doSystemBack();
                } else if (toolbar.getNavigationButtonState() == Toolbar.NavigationButtonState.Menu) {
                    fragmentController.getDrawerLayout().openDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onTextChanged(CharSequence text) {
            }

            @Override
            public void onSearchPressed(CharSequence text) {
            }
        });
    }

    public List<Animator> getEnterAnimators(View container) {
        List<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(container, "alpha", 0.0f, 1.0f));
        animators.add(ObjectAnimator.ofFloat(container, "translationX", AndroidUtils.dp(100), 0.0f));

        return animators;
    }

    public List<Animator> getExitAnimators(View container) {
        List<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(container, "alpha", 1.0f, 0.0f));
        animators.add(ObjectAnimator.ofFloat(container, "translationX", 0.0f, AndroidUtils.dp(100)));

        return animators;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    protected LayoutInflater inflater() {
        return LayoutInflater.from(fragmentController.getActivity());
    }

    protected Resources getResources() {
        return fragmentController.getActivity().getResources();
    }

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    public void onFragmentCreate() {
    }

    public void onFragmentDestroy() {
    }

    public void onResume() {
    }

    public void onPause() {
        if (toolbar != null) {
            if (toolbar.isSearchFieldActive()) {
                toolbar.toggleSearchView();
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    /**
     * @return whether the event was consumed.
     */
    public boolean onBackPressed() {
        return fragmentController.doSystemBack();
    }

    public void onDatabaseReceived(Database database) {
    }

    public void saveSelf(Bundle args) {
    }

    public void restoreSelf(Bundle args) {
    }

}
