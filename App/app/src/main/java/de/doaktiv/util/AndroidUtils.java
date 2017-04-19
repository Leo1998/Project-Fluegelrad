package de.doaktiv.util;

import android.content.res.Resources;
import android.util.TypedValue;

import de.doaktiv.android.DoaktivApplication;

public class AndroidUtils {

    private static Resources res;

    static {
        res = DoaktivApplication.applicationInstance.getResources();
    }

    public static int dp(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

}
