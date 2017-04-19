package de.doaktiv.android.base;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.doaktiv.R;
import de.doaktiv.util.AndroidUtils;

public class Toolbar extends LinearLayout {

    public enum NavigationButtonState {
        Back, Menu;
    }

    public interface ToolbarListener {
        void onMenuItemClick(int id);
    }

    private MenuDrawable menuDrawable;
    private ImageView navigationButtonImageView;
    private NavigationButtonState navigationButtonState;
    private TextView titleTextView;

    private List<ToolbarListener> listeners = new ArrayList<>();

    public Toolbar(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        this.setOrientation(HORIZONTAL);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getToolbarHeight()));

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
        LayoutParams layoutParams = new LayoutParams(getToolbarHeight(), getToolbarHeight());
        addView(this.navigationButtonImageView, layoutParams);
        setNavigationButtonState(NavigationButtonState.Back);

        this.titleTextView = new TextView(context);
        this.titleTextView.setText("");
        this.titleTextView.setTextAppearance(context, R.style.TitleTextLight);
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.leftMargin = AndroidUtils.dp(3);
        addView(titleTextView, layoutParams);
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

    public void setTitleText(String titleText) {
        this.titleTextView.setText(titleText);
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
