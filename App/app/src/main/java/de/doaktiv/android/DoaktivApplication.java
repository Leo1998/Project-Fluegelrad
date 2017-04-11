package de.doaktiv.android;

import android.app.Application;
import android.util.Log;

import java.io.File;

import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.DatabaseUpdateListener;

public class DoaktivApplication extends Application {

    private static final String TAG = "DoaktivApplication";

    private DatabaseManager databaseManager;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");

        this.databaseManager = new DatabaseManager(new File(getApplicationContext().getFilesDir(), "database"), new DatabaseUpdateListener() {
            @Override
            public void onDatabaseChanged() {
                //refreshFragments();
            }
        });
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
