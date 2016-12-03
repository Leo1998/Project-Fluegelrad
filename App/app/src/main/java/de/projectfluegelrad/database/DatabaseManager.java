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
import java.io.FilenameFilter;
import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.logging.Logger;
import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager implements Runnable {

    private final View attachedView;
    private final File filesDirectory;

    private final List<Event> eventList = new ArrayList<Event>();
    private User user;

    private Logger logger;

    private boolean running = false;
    private Object waitLock = new Object();
    private DatabaseRequest currentRequest = null;

    public DatabaseManager(View attachedView, File filesDirectory) {
        this.attachedView = attachedView;
        this.filesDirectory = filesDirectory;
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }

        this.logger = new SnackbarLogger(attachedView);

        new Thread(this, "Database Service").start();
    }

    @Override
    public void run() {
        login();

        readEvents();

        refreshEventData();

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
                refreshEventData();
                break;
            case SaveEventList:
                saveEvents();
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

    private void refreshEventData() {
        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        try {
            if (!isConnected) {
                throw new IllegalStateException(attachedView.getContext().getResources().getText(R.string.network_failure).toString());
            }

            URL dbServiceURL = new URL("http://fluegelrad.ddns.net/recieveDatabase.php?u=" + user.getId() + "&t=" + user.getHashedToken());
            URLConnection c = dbServiceURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

            StringBuilder jsonBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                jsonBuilder.append(inputLine);
            in.close();

            System.out.println(jsonBuilder.toString());

            JSONArray rootArray = new JSONArray(new JSONTokener(jsonBuilder.toString()));
            JSONArray array = rootArray.getJSONArray(1);

            String newToken = rootArray.getJSONArray(0).getString(0);
            user.setNewToken(newToken);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Event event = Event.readEvent(obj);

                registerEvent(event);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        sortEvents();
        saveEvents();
    }

    private void readEvents() {
        File[] eventFiles = filesDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".event");
            }
        });

        for (int i = 0; i < eventFiles.length; i++) {
            File file = eventFiles[i];

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                String json = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json += line;
                }
                reader.close();

                JSONObject obj = new JSONObject(new JSONTokener(json));

                Event event = Event.readEvent(obj);

                registerEvent(event);
            } catch(Exception e) {
                 e.printStackTrace();
            }
        }

        sortEvents();
    }

    private void saveEvents() {
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);

            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filesDirectory, event.getId() + ".event"))));

                writer.write(Event.writeEvent(event).toString());
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                return e1.getDate().compareTo(e2.getDate());
            }
        });
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void stopDatabaseService() {
        running = false;
    }

    public void destroy() {
        saveEvents();

        running = false;
    }
}
