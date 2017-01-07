package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.logging.Logger;
import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager implements Runnable {

    public static DatabaseManager INSTANCE;

    private final DatabaseUpdateListener updateListener;
    private final View attachedView;
    private final File filesDirectory;

    private final List<Event> eventList = new ArrayList<Event>();
    private final List<Image> images = new ArrayList<>();
    private final List<Sponsor> sponsorList = new ArrayList<Sponsor>();
    private User user;

    private Logger logger;

    private boolean running = false;
    private Object waitLock = new Object();
    private DatabaseRequest currentRequest = null;
    private DatabaseRequestListener currentListener = null;

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

        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {}
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

            if (currentListener != null)
                currentListener.onFinish();

            currentRequest = null;
            currentListener = null;
        }
    }

    private void login() {
        if (user == null) {
            int attempt = 0;

            while(attempt < 2) {
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

                    if (userJson.startsWith("Error:"))
                        throw new DatabaseException(userJson);

                    JSONArray array = new JSONArray(new JSONTokener(userJson));
                    int id = array.getInt(0);
                    String token = array.getString(1);

                    this.user = new User(id, token);
                    attempt = Integer.MAX_VALUE;
                } catch(Exception e) {
                    e.printStackTrace();

                    this.user = null;
                    new File(filesDirectory, "user.dat").delete();

                    attempt++;
                }
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
        this.currentListener = listener;

        synchronized (waitLock) {
            waitLock.notify();
        }

        while (wait && this.currentRequest == request) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     *
     * @param scriptAddress the scripts address (without arguments)
     * @return the json text
     * @throws DatabaseException
     */
    private String executeScript(String scriptAddress, Map<String, String> args) throws Exception {
        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new DatabaseException(attachedView.getContext().getResources().getText(R.string.network_failure).toString());
        }

        String address = scriptAddress + "?u=" + user.getId() + "&t=" + user.getHashedToken();
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.equals("u") || key.equals("t"))
                    continue;

                address += "&" + key + "=" + args.get(key);
            }
        }

        URL url = new URL(address);
        URLConnection c = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));

        StringBuilder jsonBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            jsonBuilder.append(inputLine);
        in.close();

        String raw = jsonBuilder.toString();
        if (raw.startsWith("Error:")) {
            if (raw.equals("Error: Invalid Token") || raw.equals("Error: Unknown ID")) {
                // fix wrong user
                this.user = null;
                new File(filesDirectory, "user.dat").delete();
                login();
                logger.log("Invalid login(retrying)");
                return executeScript(scriptAddress, args);
            } else {
                throw new DatabaseException(raw);
            }
        }

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
            String json = executeScript("http://fluegelrad.ddns.net/recieveDatabase.php", null);

            readDatabase(json, true);
        } catch(Exception e) {
            logger.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void readDatabase(String json, boolean save) throws JSONException, ParseException {
        JSONObject root = new JSONObject(new JSONTokener(json));

        JSONArray eventDataArray = root.getJSONArray("events");
        JSONArray imageAtlasArray = root.getJSONArray("images");
        JSONArray sponsorDataArray = root.getJSONArray("sponsors");

        for (int i = 0; i < eventDataArray.length(); i++) {
            JSONObject obj = eventDataArray.getJSONObject(i);

            Event event = Event.readEvent(obj);

            //Log.i("DatabaseManager", "Event: " + event.toString());

            registerEvent(event);
        }

        images.clear();
        for (int i = 0; i < imageAtlasArray.length(); i++) {
            JSONObject obj = imageAtlasArray.getJSONObject(i);

            Image image = Image.readImage(obj);

            //Log.i("DatabaseManager", "Image: " + image.toString());

            images.add(image);
        }

        for (int i = 0; i < sponsorDataArray.length(); i++) {
            JSONObject obj = sponsorDataArray.getJSONObject(i);

            Sponsor sponsor = Sponsor.readSponsor(obj);

            //Log.i("DatabaseManager", "Sponsor: " + sponsor.toString());

            registerSponsor(sponsor);
        }

        sortEvents();


        if (save)
            saveDatabaseToStorage();

        updateListener.onDatabaseChanged();
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
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);

                JSONObject obj = Image.writeImage(image);

                imageAtlasArray.put(obj);
            }

            JSONArray sponsorDataArray = new JSONArray();
            for (int i = 0; i < sponsorList.size(); i++) {
                Sponsor sponsor = sponsorList.get(i);

                JSONObject obj = Sponsor.writeSponsor(sponsor);

                sponsorDataArray.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("events", eventDataArray);
            root.put("images", imageAtlasArray);
            root.put("sponsors", sponsorDataArray);

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

    private void registerSponsor(Sponsor sponsor) {
        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor currentSponsor= sponsorList.get(i);

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

    public boolean signInForEvent(Event event) {
        try {
            Map<String, String> args = new HashMap<>();
            args.put("k", Integer.toString(event.getId()));

            String result = executeScript("http://fluegelrad.ddns.net/sendDatabase.php", args);

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sortEvents() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getDateStart().compareTo(e2.getDateStart());
            }
        });
    }

    public void stopDatabaseService() {
        running = false;
    }

    public void destroy() {
        saveDatabaseToStorage();

        running = false;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public List<Image> getImages() {
        return images;
    }

    public List<Sponsor> getSponsorList() {
        return sponsorList;
    }

    public List<Event> getRecentEventList() {
        List<Event> list = new ArrayList<>();

        for (int i = eventList.size()-1; i >= 0; i--){
            if (eventList.get(i).getDateStart().compareTo(Calendar.getInstance()) > 0){
                list.add(eventList.get(i));
            } else {
                break;
            }
        }

        Collections.reverse(list);

        return list;
    }

    public ArrayList<Image> getImages(Event event) {
        ArrayList<Image> list = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (image.getEventId() == event.getId()) {
                list.add(image);
            }
        }

        return list;
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

    public Image getImage(String imagePath) {
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (image.getPath().equals(imagePath)) {
                return image;
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
