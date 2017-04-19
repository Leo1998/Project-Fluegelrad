package de.doaktiv.android.base;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.doaktiv.util.AndroidUtils;

public class Toolbar extends FrameLayout {

    public enum NavigationButtonState {
        Back, Menu;
    }

    public interface ToolbarListener {
        void onMenuItemClick(int id);
    }

    private MenuDrawable menuDrawable;
    private ImageView navigationButtonImageView;
    private NavigationButtonState navigationButtonState;
    private List<ToolbarListener> listeners = new ArrayList<>();

    public Toolbar(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getToolbarHeight()));

        this.menuDrawable = new MenuDrawable();

        this.navigationButtonImageView = new ImageView(context);
        this.navigationButtonImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.navigationButtonImageView.setImageDrawable(menuDrawable);
        this.navigationButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ToolbarListener listener : listeners)
                    listener.onMenuItemClick(-1);
            }
        });
        addView(this.navigationButtonImageView, new LayoutParams(getToolbarHeight(), getToolbarHeight(), Gravity.LEFT | Gravity.TOP));

        setNavigationButtonState(NavigationButtonState.Back);
    }

    public NavigationButtonState getNavigationButtonState() {
        return navigationButtonState;
    }

    public void setNavigationButtonState(NavigationButtonState navigationButtonState) {
        this.navigationButtonState = navigationButtonState;

        if (navigationButtonState == NavigationButtonState.Back)
            this.menuDrawable.setRotation(1, true);
        else if (navigationButtonState == NavigationButtonState.Menu)
            this.menuDrawable.setRotation(0, true);
    }

    public List<ToolbarListener> getListeners() {
        return listeners;
    }

    public void addListener(ToolbarListener listener) {
        this.listeners.add(listener);
    }

    public static int getToolbarHeight() {
        return AndroidUtils.dp(56);//TODO
    }

}
