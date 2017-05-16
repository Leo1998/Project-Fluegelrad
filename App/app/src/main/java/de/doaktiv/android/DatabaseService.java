package de.doaktiv.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import de.doaktiv.R;
import de.doaktiv.database.Database;
import de.doaktiv.database.DatabaseDownloadTask;
import de.doaktiv.database.DatabaseException;
import de.doaktiv.database.DatabaseLoginTask;
import de.doaktiv.database.DatabaseParticipateTask;
import de.doaktiv.database.DatabaseRateTask;
import de.doaktiv.database.DatabaseTaskObserver;
import de.doaktiv.database.DatabaseTaskWorker;
import de.doaktiv.database.Event;
import de.doaktiv.database.Sponsor;
import de.doaktiv.database.User;
import de.doaktiv.util.AndroidUtils;

public class DatabaseService extends Service {

    public class LocalBinder extends Binder {
        public DatabaseService getService() {
            return DatabaseService.this;
        }
    }

    private static final String TAG = "DatabaseService";

    private final IBinder binder = new LocalBinder();

    /**
     * the directory to store the Database files
     */
    private File filesDirectory;
    /**
     * the local database
     */
    private Database database;
    /**
     * the Database user account
     */
    private User user;
    private DatabaseTaskWorker worker;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");

        this.filesDirectory = new File(getApplicationContext().getFilesDir(), "database");
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }

        File databaseFile = new File(filesDirectory, "database.dat");
        this.database = new Database(databaseFile);

        this.worker = new DatabaseTaskWorker(this);

        worker.execute(new DatabaseLoginTask(), new DatabaseTaskObserver() {
            @Override
            public void onFinish(Object result) {
                assert (result != null && result instanceof User);

                DatabaseService.this.user = (User) result;

                Log.i(TAG, "Logged in as: " + DatabaseService.this.user.toString());

                downloadDatabase(null);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");

        this.worker.stop();
        saveDatabaseToStorage();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void downloadDatabase(final Runnable after) {
        worker.execute(new DatabaseDownloadTask(), new DatabaseTaskObserver() {
            @Override
            public void onFinish(Object result) {
                Intent intent = new Intent("de.doaktiv.databaseUpdate");
                sendBroadcast(intent);

                if (after != null)
                    after.run();
            }
        });
    }

    public void participateEvent(Event event, final Runnable after) {
        worker.execute(new DatabaseParticipateTask(event), new DatabaseTaskObserver() {
            @Override
            public void onFinish(Object result) {
                boolean success = (Boolean) result;

                if (after != null)
                    after.run();
            }
        });
    }

    public void rateEvent(Event event, int rating, final Runnable after) {
        DatabaseRateTask.RateParamsWrapper wrapper = new DatabaseRateTask.RateParamsWrapper(event, rating);

        event.rate(rating);

        worker.execute(new DatabaseRateTask(wrapper), new DatabaseTaskObserver() {
            @Override
            public void onFinish(Object result) {
                boolean success = (Boolean) result;

                if (after != null)
                    after.run();
            }
        });
    }

    public File getFilesDirectory() {
        return filesDirectory;
    }

    public Database getDatabase() {
        return database;
    }

    public User getUser() {
        return user;
    }

    /**
     * writes the Database to a file on device storage
     */
    public void saveDatabaseToStorage() {
        File eventFile = new File(filesDirectory, "database.dat");

        try {
            List<Event> eventList = database.getEventList();
            JSONArray eventDataArray = new JSONArray();
            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);

                JSONObject obj = Event.writeEvent(event);

                eventDataArray.put(obj);
            }

            List<Sponsor> sponsorList = database.getSponsorList();
            JSONArray sponsorDataArray = new JSONArray();
            for (int i = 0; i < sponsorList.size(); i++) {
                Sponsor sponsor = sponsorList.get(i);

                JSONObject obj = Sponsor.writeSponsor(sponsor);

                sponsorDataArray.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("events", eventDataArray);
            root.put("sponsors", sponsorDataArray);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(eventFile)));

            writer.write(root.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param scriptAddress the scripts address (without arguments)
     * @param args          arguments for the script
     * @return the json text
     * @throws DatabaseException
     */
    public String executeScript(String scriptAddress, Map<String, String> args) throws Exception {
        if (!AndroidUtils.isNetworkConnected())
            throw new DatabaseException(getResources().getText(R.string.network_failure).toString());
        if (user == null)
            throw new DatabaseException(getResources().getText(R.string.database_access_failure).toString());

        // append user data
        String address = scriptAddress + "?u=" + user.getId() + "&t=" + user.getHashedToken();

        //append arguments
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.equals("u") || key.equals("t"))
                    continue;

                address += "&" + key + "=" + args.get(key);
            }
        }

        // build connection
        URL url = new URL(address);
        URLConnection c = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

        StringBuilder jsonBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            jsonBuilder.append(inputLine);
        in.close();

        // the raw result
        String raw = jsonBuilder.toString();
        if (raw.startsWith("Error:")) {
            throw new DatabaseException(raw);
        }

        // split header and data
        String header = raw.split(",")[0];
        String json = raw.substring(header.length() + 1);

        // read header and refresh security token
        JSONArray headerArray = new JSONArray(new JSONTokener(header));
        String newToken = headerArray.getString(0);
        refreshToken(newToken);

        return json;
    }

    /**
     * updates the users security token after a protected script was executed
     *
     * @param newToken
     */
    private void refreshToken(String newToken) {
        user.setNewToken(newToken);

        File userFile = new File(filesDirectory, "user.dat");

        try {
            JSONArray array = new JSONArray();
            array.put(user.getId());
            array.put(user.getHashedToken());

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile)));
            writer.write(array.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
