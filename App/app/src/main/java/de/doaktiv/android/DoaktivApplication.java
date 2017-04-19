package de.doaktiv.android;

import android.app.Application;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.doaktiv.database.Database;
import de.doaktiv.database.DatabaseManager;
import de.doaktiv.database.DatabaseReceiver;

public class DoaktivApplication extends Application implements DatabaseReceiver {

    public static DoaktivApplication applicationInstance = null;

    private static final String TAG = "DoaktivApplication";

    private DatabaseManager databaseManager;

    private List<DatabaseReceiver> receivers = new ArrayList<DatabaseReceiver>();

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;

        Log.i(TAG, "onCreate");

        File filesDirectory = new File(getApplicationContext().getFilesDir(), "database");
        this.databaseManager = new DatabaseManager(filesDirectory, this);
    }

    public void registerReceiver(DatabaseReceiver receiver) {
        receivers.add(receiver);
    }

    public void unregisterReceiver(DatabaseReceiver receiver) {
        receivers.remove(receiver);
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    @Override
    public void onReceive(Database database) {
        Log.i(TAG, "Recieved " + database.getEventCount() + " Events and " + database.getSponsorCount() + " Sponsors!");

        for (int i = 0; i < receivers.size(); i++) {
            receivers.get(i).onReceive(database);
        }
    }
}
