package de.doaktiv.android.base;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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

        void onBackPressed();

        void onTextChanged(CharSequence text);

        void onSearchPressed(CharSequence text);

    }

    private MenuDrawable menuDrawable;
    private ImageView navigationButtonImageView;
    private NavigationButtonState navigationButtonState;
    private TextView titleTextView;
    private EditText searchField;
    private ImageView searchButton;

    private boolean isSearchFieldActive = false;
    private NavigationButtonState tmpState = NavigationButtonState.Back;

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
                if (isSearchFieldActive) {
                    toggleSearchView();
                } else {
                    for (ToolbarListener listener : listeners)
                        listener.onBackPressed();
                }
            }
        });
        LayoutParams layoutParams = new LayoutParams(getToolbarHeight(), getToolbarHeight());
        addView(this.navigationButtonImageView, layoutParams);
        setNavigationButtonState(NavigationButtonState.Back);

        FrameLayout container = new FrameLayout(context);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.LEFT;
        addView(container, layoutParams);

        this.titleTextView = new TextView(context);
        this.titleTextView.setText("");
        this.titleTextView.setTextAppearance(context, R.style.TitleTextLight);
        this.titleTextView.setGravity(Gravity.CENTER_VERTICAL);
        container.addView(titleTextView, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL));

        this.searchField = new EditText(context);
        this.searchField.setText("");
        this.searchField.setTextAppearance(context, R.style.TitleTextLight);
        this.searchField.setSingleLine(true);
        this.searchField.setBackgroundResource(0);
        this.searchField.setPadding(0, 0, 0, 0);
        this.searchField.setTextIsSelectable(false);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_SEARCH || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    for (ToolbarListener listener : listeners)
                        listener.onSearchPressed(searchField.getText());

                    searchField.setText("");
                    searchField.requestFocus();
                    AndroidUtils.hideKeyboard(searchField);
                }
                return false;
            }
        });
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (ToolbarListener listener : listeners)
                    listener.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        this.searchField.setVisibility(GONE);
        container.addView(searchField, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.LEFT));

        this.searchButton = new ImageView(context);
        this.searchButton.setScaleType(ImageView.ScaleType.CENTER);
        this.searchButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_search_white));
        this.searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSearchFieldActive)
                    toggleSearchView();
            }
        });
        this.searchButton.setVisibility(GONE);
        container.addView(searchButton, new FrameLayout.LayoutParams(getToolbarHeight(), getToolbarHeight(), Gravity.RIGHT));
    }

    public NavigationButtonState getNavigationButtonState() {
        return navigationButtonState;
    }

    public void setNavigationButtonState(NavigationButtonState navigationButtonState) {
        setNavigationButtonState(navigationButtonState, true);
    }

    public void setNavigationButtonState(NavigationButtonState navigationButtonState, boolean animate) {
        this.navigationButtonState = navigationButtonState;

        if (navigationButtonState == NavigationButtonState.Back)
            this.menuDrawable.setRotation(1, animate);
        else if (navigationButtonState == NavigationButtonState.Menu)
            this.menuDrawable.setRotation(0, animate);
    }

    public boolean isSearchFieldActive() {
        return isSearchFieldActive;
    }

    public void toggleSearchView() {
        if (isSearchFieldActive) {
            this.isSearchFieldActive = false;

            searchField.setText("");
            searchField.requestFocus();
            AndroidUtils.hideKeyboard(searchField);
            setNavigationButtonState(tmpState, true);

            searchField.setVisibility(GONE);
            searchButton.setVisibility(VISIBLE);
            titleTextView.setVisibility(VISIBLE);
        } else {
            this.isSearchFieldActive = true;

            this.tmpState = getNavigationButtonState();
            setNavigationButtonState(NavigationButtonState.Back, true);

            searchField.setVisibility(VISIBLE);
            searchButton.setVisibility(GONE);
            titleTextView.setVisibility(GONE);

            searchField.setText("");
            searchField.requestFocus();
            AndroidUtils.showKeyboard(searchField);
        }
    }

    public void setSearchButtonEnabled(boolean enabled) {
        if (!isSearchFieldActive) {
            this.searchButton.setVisibility(enabled ? VISIBLE : GONE);
        }
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
