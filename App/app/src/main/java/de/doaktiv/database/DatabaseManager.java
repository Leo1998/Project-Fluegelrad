package de.doaktiv.database;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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

    /**
     * static reference on the DatabaseManager
     */
    public static DatabaseManager INSTANCE;

    /**
     * listener for changes in the database
     */
    private final DatabaseUpdateListener updateListener;
    /**
     * the directory to store the Database files
     */
    private final File filesDirectory;
    /**
     * list of all events
     */
    private final List<Event> eventList = new ArrayList<Event>();
    /**
     * list of all sponsors
     */
    private final List<Sponsor> sponsorList = new ArrayList<Sponsor>();
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
     * @param updateListener
     */
    public DatabaseManager(File filesDirectory, DatabaseUpdateListener updateListener) {
        if (INSTANCE != null)
            throw new IllegalStateException("Only one Instance allowed!");
        INSTANCE = this;

        this.filesDirectory = filesDirectory;
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }
        this.updateListener = updateListener;

        firstLogin();
    }

    /**
     * reads the local copy of the database and then tries to login and recieve the database from the server
     */
    private void firstLogin() {
        readDatabaseFromStorage();

        executeTask(new DatabaseLoginTask(), null, new DatabaseTaskWatcher() {
            @Override
            public void onFinish(Object result) {
                assert (result != null && result instanceof User);

                DatabaseManager.this.user = (User) result;

                Log.i("DatabaseManager", "Logged in as: " + DatabaseManager.this.user.toString());

                executeTask(new DatabaseDownloadTask(), null, null);
            }
        });
    }

    /**
     * reads the Database from the file on device storage
     */
    private void readDatabaseFromStorage() {
        File databaseFile = new File(filesDirectory, "database.dat");

        if (databaseFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(databaseFile)));

                String json = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json += line;
                }
                reader.close();

                readDatabase(json, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * writes the Database to a file on device storage
     */
    public void saveDatabaseToStorage() {
        File eventFile = new File(filesDirectory, "database.dat");

        try {
            JSONArray eventDataArray = new JSONArray();
            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);

                JSONObject obj = Event.writeEvent(event);

                eventDataArray.put(obj);
            }

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
     * helper method for reading the Database from json text
     *
     * @param json
     * @param save
     * @throws JSONException
     * @throws ParseException
     */
    void readDatabase(String json, boolean save) throws JSONException, ParseException {
        JSONObject root = new JSONObject(new JSONTokener(json));

        JSONArray eventDataArray = root.getJSONArray("events");
        JSONArray sponsorDataArray = root.getJSONArray("sponsors");

        for (int i = 0; i < eventDataArray.length(); i++) {
            JSONObject obj = eventDataArray.getJSONObject(i);

            Event event = Event.readEvent(obj);

            registerEvent(event);
        }

        for (int i = 0; i < sponsorDataArray.length(); i++) {
            JSONObject obj = sponsorDataArray.getJSONObject(i);

            Sponsor sponsor = Sponsor.readSponsor(obj);

            registerSponsor(sponsor);
        }

        sortEvents();

        if (save)
            saveDatabaseToStorage();

        updateListener.onDatabaseChanged();
    }

    /**
     * called when a new event is registered
     *
     * @param event
     */
    private void registerEvent(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            Event currentEvent = eventList.get(i);

            if (currentEvent.equalsId(event)) {
                if (currentEvent == event) {
                    // already exists
                    return;
                } else {
                    // update
                    eventList.remove(currentEvent);

                    if (currentEvent.isParticipating()) {
                        event.participate(false);
                    }

                    break;
                }
            }
        }

        eventList.add(event);
    }

    /**
     * called when a new sponsor is registered
     *
     * @param sponsor
     */
    private void registerSponsor(Sponsor sponsor) {
        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor currentSponsor = sponsorList.get(i);

            if (currentSponsor.equalsId(sponsor)) {
                if (currentSponsor.equals(sponsor)) {
                    // already exists
                    return;
                } else {
                    // update
                    sponsorList.remove(currentSponsor);
                    break;
                }
            }
        }

        sponsorList.add(sponsor);
    }

    /**
     * sorts the eventList by dateStart
     */
    private void sortEvents() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getDateStart().compareTo(e2.getDateStart());
            }
        });
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

    public List<Event> getEventList() {
        return eventList;
    }

    public List<Sponsor> getSponsorList() {
        return sponsorList;
    }

    /**
     * @return alist of recent events
     */
    public List<Event> getRecentEventList() {
        List<Event> list = new ArrayList<>();

        for (int i = eventList.size() - 1; i >= 0; i--) {
            if (eventList.get(i).getDateStart().compareTo(Calendar.getInstance()) > 0) {
                list.add(eventList.get(i));
            } else {
                break;
            }
        }

        Collections.reverse(list);

        return list;
    }

    /**
     * @return a list of events prepared for the home view
     */
    public List<Event> getHomeEventList() {
        List<Event> recent = getRecentEventList();
        List<Event> shown = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            if (recent.size() >= 1) {
                shown.add(recent.get(0));
                recent.remove(0);
            }

            if (recent.size() >= 1) {
                shown.add(recent.get(0));
                recent.remove(0);
            }
        }

        for (int i = 0; i < 1; i++) {
            if (recent.size() >= 1) {
                Event mostPopular = recent.get(0);

                for (Event event : recent) {
                    if (event.getParticipants() > mostPopular.getParticipants()) {
                        mostPopular = event;
                    }
                }

                shown.add(mostPopular);
                recent.remove(mostPopular);
            }
        }

        return shown;
    }

    public List<Sponsor> getSponsors(Event event) {
        int[] sponsorIds = event.getSponsors();
        List<Sponsor> sponsors = new ArrayList<>();

        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor sponsor = sponsorList.get(i);

            for (int j = 0; j < sponsorIds.length; j++) {
                if (sponsor.getId() == sponsorIds[j]) {
                    sponsors.add(sponsor);
                }
            }
        }

        return sponsors;
    }

    public Event getEvent(int eventId) {
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);

            if (event.getId() == eventId) {
                return event;
            }
        }

        return null;
    }

    public Sponsor getSponsor(int sponsorId) {
        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor sponsor = sponsorList.get(i);

            if (sponsor.getId() == sponsorId) {
                return sponsor;
            }
        }

        return null;
    }

}
