package de.doaktiv.database;

import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * the DatabaseManager is the main class of the database system
 */
public class DatabaseManager {

    /**
     * a wrapper to link a running {@link DatabaseTask} to its {@link AsyncTask}
     */
    private class RunningTaskWrapper {
        DatabaseTask<?, ?> databaseTask;
        AsyncTask asyncTask;
    }

    private static final String TAG = "DatabaseManager";

    /**
     * static reference on the DatabaseManager
     */
    public static DatabaseManager INSTANCE;

    /**
     * the receiver
     */
    private DatabaseReceiver receiver;
    /**
     * the local database
     */
    private final Database database;
    /**
     * the directory to store the Database files
     */
    private final File filesDirectory;
    /**
     * the Database user account
     */
    private User user;
    /**
     * list of all running tasks
     */
    private final List<RunningTaskWrapper> runningTasks = new ArrayList<>();

    /**
     * Constructor.
     *
     * @param filesDirectory
     */
    public DatabaseManager(File filesDirectory, DatabaseReceiver receiver) {
        if (INSTANCE != null)
            throw new IllegalStateException("Only one Instance allowed!");
        INSTANCE = this;

        this.filesDirectory = filesDirectory;
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }

        this.receiver = receiver;

        Log.i(TAG, "firstLogin");

        File databaseFile = new File(filesDirectory, "database.dat");
        this.database = new Database(databaseFile);
        if (this.receiver != null)
            this.receiver.onReceive(this.database);

        executeTask(new DatabaseLoginTask(), null, new DatabaseTaskWatcher() {
            @Override
            public void onFinish(Object result) {
                assert (result != null && result instanceof User);

                DatabaseManager.this.user = (User) result;

                Log.i(TAG, "Logged in as: " + DatabaseManager.this.user.toString());

                executeTask(new DatabaseDownloadTask(), null, null);
            }
        });
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
     * @param task
     * @param watcher
     */
    public void executeTask(final DatabaseTask task, final Object[] params, final DatabaseTaskWatcher watcher) {
        // wrapper
        final RunningTaskWrapper wrapper = new RunningTaskWrapper();

        AsyncTask<Object, Void, Object> asyncTask = new AsyncTask<Object, Void, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                return task.execute(DatabaseManager.this, params);
            }

            @Override
            protected void onPostExecute(Object result) {
                if (watcher != null)
                    watcher.onFinish(result);

                DatabaseManager.this.runningTasks.remove(wrapper);
            }
        };

        // register in running tasks
        wrapper.databaseTask = task;
        wrapper.asyncTask = asyncTask;
        this.runningTasks.add(wrapper);

        // execute it
        asyncTask.execute(params);
    }

    /**
     * @param scriptAddress the scripts address (without arguments)
     * @param args          arguments for the script
     * @return the json text
     * @throws DatabaseException
     */
    String executeScript(String scriptAddress, Map<String, String> args) throws Exception {
        // TODO:check network
        /*ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new DatabaseException(attachedView.getContext().getResources().getText(R.string.network_failure).toString());
        }*/

        if (user == null) {
            //throw new DatabaseException(attachedView.getContext().getResources().getText(R.string.database_access_failure).toString());
        }

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
            if (raw.equals("Error: Invalid Token") || raw.equals("Error: Unknown ID")) {
                // fix wrong user
                this.user = null;
                new File(filesDirectory, "user.dat").delete();

                executeTask(new DatabaseLoginTask(), null, new DatabaseTaskWatcher() {
                    @Override
                    public void onFinish(Object result) {
                        assert (result != null && result instanceof User);

                        DatabaseManager.this.user = (User) result;

                        Log.i("DatabaseManager", "Logged in as: " + DatabaseManager.this.user.toString());
                    }
                });
            } else {
                // error!!!
                throw new DatabaseException(raw);
            }
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

    /**
     * stops the DatabaseService and saves all data
     */
    public void destroy() {
        saveDatabaseToStorage();

        INSTANCE = null;
    }

    public User getUser() {
        return user;
    }

    public File getFilesDirectory() {
        return filesDirectory;
    }

    public Database getDatabase() {
        return database;
    }

    public DatabaseReceiver getReceiver() {
        return receiver;
    }
}
