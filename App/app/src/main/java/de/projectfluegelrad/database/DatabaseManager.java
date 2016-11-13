package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager implements Runnable {

    private final View attachedView;
    private final File filesDirectory;

    private QueryExecuter queryExecuter;

    private final List<Event> eventList = new ArrayList<Event>();

    private boolean running = false;
    private Object waitLock = new Object();
    private DatabaseRequest currentRequest = null;

    public DatabaseManager(View attachedView, File filesDirectory) {
        this.attachedView = attachedView;
        this.filesDirectory = filesDirectory;
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs();
        }

        new Thread(this, "Database Service").start();
    }

    @Override
    public void run() {
        readEvents();

        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        this.queryExecuter = new QueryExecuter(attachedView.getContext(), new SnackbarLogger(attachedView), cm, new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"), "testuser", "123456");

        if (this.queryExecuter.connect()) {
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
        ResultSet result = this.queryExecuter.executeQuery(new BasicQuery("SELECT * FROM events;"));

        try {
            ResultSetMetaData metaData = result.getMetaData();

            while (result.next()) {
                if (metaData.getColumnCount() != 10) {
                    throw new DatabaseException("Bad Event Data! (column count == " + metaData.getColumnCount() + ")");
                }

                int id = result.getInt("id");
                String location = result.getString("location");
                String category = result.getString("category");
                int price = result.getInt("price");
                String host = result.getString("host");
                Date date = new Date(result.getTimestamp("date").getTime());
                String description = result.getString("description");
                int maxParticipants = result.getInt("maxParticipants");
                int participants = result.getInt("participants");
                int age = result.getInt("age");

                Event event = new Event(id, location, category, price, host, date, description, maxParticipants, participants, age);

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

            Event event = null;
            try {
                event = Event.readEvent(new FileInputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            registerEvent(event);
        }

        sortEvents();
    }

    private void saveEvents() {
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);

            try {
                Event.writeEvent(new FileOutputStream(new File(filesDirectory, event.getId() + ".event")), event);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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

        queryExecuter.closeConnection();
    }
}
