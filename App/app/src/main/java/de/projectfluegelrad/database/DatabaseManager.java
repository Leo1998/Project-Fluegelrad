package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.View;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import de.projectfluegelrad.database.logging.SnackbarLogger;

public class DatabaseManager {

    private View attachedView;

    private QueryExecuter queryExecuter;

    public DatabaseManager(View attachedView) {
        this.attachedView = attachedView;

        ConnectivityManager cm = (ConnectivityManager) attachedView.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        this.queryExecuter = new QueryExecuter(new SnackbarLogger(attachedView), cm, new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"), "testuser", "123456");

        if (this.queryExecuter.connectAndWait()) {
            loadEventData();
        }
    }

    public void loadEventData() {
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

                /*StringBuilder builder = new StringBuilder();
                builder.append(id);
                builder.append(location);
                builder.append(category);
                builder.append(price);
                builder.append(host);
                builder.append(date);
                builder.append(description);
                System.out.println(builder.toString());*/
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
