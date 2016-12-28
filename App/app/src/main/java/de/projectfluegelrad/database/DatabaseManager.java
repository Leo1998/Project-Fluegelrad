package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.logging.Logger;
import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager implements Runnable {

    public static DatabaseManager INSTANCE;

    private final DatabaseUpdateListener updateListener;
    private final View attachedView;
    private final File filesDirectory;

    private final List<Event> eventList = new ArrayList<Event>();
    private final ImageAtlas imageAtlas = new ImageAtlas();
    private User user;

    private Logger logger;

    private boolean running = false;
    private Object waitLock = new Object();
    private DatabaseRequest currentRequest = null;

    public DatabaseManager(View attachedView, File filesDirectory, DatabaseUpdateListener updateListener) {
        if (INSTANCE != null)
            throw new IllegalStateException("Only one Instance allowed!");
        INSTANCE = this;

        this.attachedView = attachedView;
        this.filesDirectory = filesDirectory;
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }
        this.updateListener = updateListener;

        this.logger = new SnackbarLogger(attachedView);

        new Thread(this, "Database Service").start();
    }

    @Override
    public void run() {
        readDatabaseFromStorage();

        login();

        readDatabaseFromServer();

        running = true;
        while (running) {
            synchronized (waitLock) {
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
            }
        }

        switch (currentRequest) {
            case RefreshEventList:
                readDatabaseFromServer();
                break;
            case SaveEventList:
                saveDatabaseToStorage();
                break;
            default:
                break;
            }

            currentRequest = null;
        }
    }

    private void login() {
        if (user == null) {
            try {
                File userFile = new File(filesDirectory, "user.dat");
                String userJson = null;
                if (userFile.exists()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(userFile)));

                    StringBuilder jsonBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        jsonBuilder.append(inputLine);
                    in.close();
                    userJson = jsonBuilder.toString();
                } else {
                    URL url = new URL("http://fluegelrad.ddns.net/createUser.php");
                    URLConnection c = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

                    StringBuilder jsonBuilder = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        jsonBuilder.append(inputLine);
                    in.close();

                    userJson = jsonBuilder.toString();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile)));
                    writer.write(userJson);
                    writer.close();
                }

                JSONArray array = new JSONArray(new JSONTokener(userJson));
                int id = array.getInt(0);
                String token = array.getString(1);

                this.user = new User(id, token);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void request(DatabaseRequest request, boolean wait) {
        request(request, wait, null);
    }

    public synchronized void request(DatabaseRequest request, boolean wait, DatabaseRequestListener listener) {
        this.currentRequest = request;

        synchronized (waitLock) {
            waitLock.notify();
        }

        while (wait && this.currentRequest == request) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        if (listener != null)
            listener.onFinish();
    }

    /**
     *
     * @param scriptAddress the scripts address (without arguments)
     * @return the json text
     * @throws DatabaseException
     */
    private String executeScript(String scriptAddress) throws Exception {
        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new DatabaseException(attachedView.getContext().getResources().getText(R.string.network_failure).toString());
        }

        String address = scriptAddress + "?u=" + user.getId() + "&t=" + user.getHashedToken();
        URL url = new URL(address);
        URLConnection c = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

        StringBuilder jsonBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            jsonBuilder.append(inputLine);
        in.close();

        String raw = jsonBuilder.toString();
        if (raw.startsWith("Error:"))
            throw new DatabaseException(raw);

        String header = raw.split(",")[0];
        String json = raw.substring(header.length() + 1);

        JSONArray headerArray = new JSONArray(new JSONTokener(header));
        String newToken = headerArray.getString(0);
        refreshToken(newToken);

        return json;
    }

    private void readDatabaseFromStorage() {
        File databaseFile = new File(filesDirectory, "database");

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

    private void readDatabaseFromServer() {
        try {
            String json = executeScript("http://fluegelrad.ddns.net/recieveDatabase.php");

            readDatabase(json, true);

            updateListener.onDatabaseChanged();
        } catch(Exception e) {
            logger.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void readDatabase(String json, boolean save) throws JSONException, ParseException {
        JSONObject root = new JSONObject(new JSONTokener(json));

        JSONArray eventDataArray = root.getJSONArray("events");
        JSONArray imageAtlasArray = root.getJSONArray("images");

        for (int i = 0; i < eventDataArray.length(); i++) {
            JSONObject obj = eventDataArray.getJSONObject(i);

            Event event = Event.readEvent(obj);

            registerEvent(event);
        }

        imageAtlas.clearImages();

        for (int i = 0; i < imageAtlasArray.length(); i++) {
            JSONObject obj = imageAtlasArray.getJSONObject(i);

            Image image = Image.readImage(obj);

            imageAtlas.addImage(image);
        }

        sortEvents();

        if (save)
            saveDatabaseToStorage();
    }

    private void saveDatabaseToStorage() {
        File eventFile = new File(filesDirectory, "database");

        try {
            JSONArray eventDataArray = new JSONArray();
            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);

                JSONObject obj = Event.writeEvent(event);

                eventDataArray.put(obj);
            }

            JSONArray imageAtlasArray = new JSONArray();
            List<Image> images = imageAtlas.getAllImages();
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);

                JSONObject obj = Image.writeImage(image);

                imageAtlasArray.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("events", eventDataArray);
            root.put("images", imageAtlasArray);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(eventFile)));

            writer.write(root.toString());
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEvent(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            Event currentEvent = eventList.get(i);

            if (currentEvent.equalsId(event)) {
                if (currentEvent.equals(event)) {
                    // already exists
                    return;
                } else {
                    // update
                    eventList.remove(currentEvent);
                    break;
                }
            }
        }

        eventList.add(event);
    }

    private void sortEvents() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getDateStart().compareTo(e2.getDateStart());
            }
        });
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public ImageAtlas getImageAtlas() {
        return imageAtlas;
    }

    public void stopDatabaseService() {
        running = false;
    }

    public void destroy() {
        saveDatabaseToStorage();

        running = false;
    }
}
