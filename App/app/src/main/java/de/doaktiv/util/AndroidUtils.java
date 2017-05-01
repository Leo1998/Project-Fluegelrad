package de.doaktiv.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import de.doaktiv.android.DoaktivApplication;

public class AndroidUtils {

    private static Resources res;

    static {
        res = DoaktivApplication.applicationInstance.getResources();
    }

    public static int dp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public static void showKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
        }
    }

    public static boolean isKeyboardShowed(View view) {
        if (view == null) {
            return false;
        }
        try {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            return inputManager.isActive(view);
        } catch (Exception e) {
        }
        return false;
    }

    public static void hideKeyboard(View view) {
        if (view == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!imm.isActive()) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) DoaktivApplication.applicationInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
