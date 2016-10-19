package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager implements Runnable {

    private final View attachedView;
    private final File filesDirectory;

    private QueryExecuter queryExecuter;

    private final List<Event> eventList = new ArrayList<Event>();

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
        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        this.queryExecuter = new QueryExecuter(new SnackbarLogger(attachedView), cm, new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"), "testuser", "123456");

        if (this.queryExecuter.connect()) {
            refreshEventData();
            saveEvents();
        }
    }

    private void refreshEventData() {
        ResultSet result = this.queryExecuter.executeQuery(new BasicQuery("SELECT * FROM events;"));

        try {
            ResultSetMetaData metaData = result.getMetaData();

            while (result.next()) {
                if (metaData.getColumnCount() != 7) {
                    throw new DatabaseException("Bad Event Data! (column count == " + metaData.getColumnCount() + ")");
                }

                int id = result.getInt("id");
                String location = result.getString("location");
                String category = result.getString("category");
                int price = result.getInt("price");
                String host = result.getString("host");
                Date date = result.getDate("date");
                String description = result.getString("description");

                Event event = new Event(id, location, category, price, host, date, description);

                System.out.println(event);

                if (!eventList.contains(event)) {
                    eventList.add(event);
                } else {
                    // already exists...
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
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

    public List<Event> getEventList() {
        return eventList;
    }
}
