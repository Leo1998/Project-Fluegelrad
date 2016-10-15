package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorTask extends DatabaseTask<DatabaseAddress, Connection> {

    public ConnectorTask(View view) {
        super(view);
    }

    @Override
    protected Connection run(DatabaseAddress[] params) {
        DatabaseAddress address = params[0];

        try {
            checkConnectivity();
        } catch(Exception e) {
            throwException(new DatabaseException("No Network!", e));
            return null;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throwException(new DatabaseException("Driver missing!", e));
            return null;
        }

        try {
            Connection c = DriverManager.getConnection(address.getUrl("testuser", "123456"));

            if (c == null) {
                throwException(new DatabaseException("Failed to connect to Database!"));
                return null;
            }

            showMessage("Connected!");

            return c;
        } catch(SQLException e) {
            throwException(new DatabaseException("Failed to connect to Database!", e));
            return null;
        }
    }

    private void checkConnectivity() throws IllegalStateException {
        ConnectivityManager cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throwException(new IllegalStateException("No Network!"));
        }
    }
}
