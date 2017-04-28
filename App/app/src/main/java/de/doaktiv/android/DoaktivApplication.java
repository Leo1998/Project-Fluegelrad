package de.doaktiv.android;

import android.app.Application;
import android.util.Log;

import org.osmdroid.config.Configuration;

import java.io.File;

import de.doaktiv.BuildConfig;

public class DoaktivApplication extends Application {

    public static DoaktivApplication applicationInstance = null;

    private static final String TAG = "DoaktivApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;

        Log.i(TAG, "onCreate");

        // setup osmdroid
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Configuration.getInstance().setOsmdroidBasePath(new File(this.getCacheDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(Configuration.getInstance().getOsmdroidBasePath(), "tiles"));
    }
}
