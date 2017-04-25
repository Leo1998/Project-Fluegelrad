package de.doaktiv.android;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class DoaktivApplication extends Application {

    public static DoaktivApplication applicationInstance = null;

    private static final String TAG = "DoaktivApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;

        Log.i(TAG, "onCreate");

        Intent intent = new Intent(this, DatabaseService.class);
        startService(intent);
    }
}
