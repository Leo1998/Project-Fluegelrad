package de.projectfluegelrad.database;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import de.projectfluegelrad.database.tasks.ConnectorTask;
import de.projectfluegelrad.database.tasks.DatabaseTask;
import de.projectfluegelrad.database.tasks.EventLoaderTask;

public class DatabaseManager {

    private View attachedView;

    private ConnectorTask connector;
    private EventLoaderTask eventLoader;

    private Connection connection;
    private Statement statement;

    public DatabaseManager(View attachedView) {
        this.attachedView = attachedView;

        this.connector = new ConnectorTask(attachedView);
        this.connector.setListener(new DatabaseTask.Listener() {
            @Override
            public void onPreRun(Object[] params) {}

            @Override
            public void onPostRun(Object o) {
                DatabaseManager.this.connection = (Connection) o;

                try {
                    DatabaseManager.this.statement = DatabaseManager.this.connection.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();//TODO
                }
            }
        });

        this.eventLoader = new EventLoaderTask(attachedView);

        connect();
    }

    private void showMessage(String msg) {
        Snackbar snackbar = Snackbar.make(attachedView, msg, 3000);
        snackbar.show();
    }

    private void connect() {
        connector.execute(new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"));
    }

}
