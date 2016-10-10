package de.projectfluegelrad.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorTask extends AsyncTask<DatabaseAddress, Void, Connection> {

    private Exception exception;

    private View view;

    public ConnectorTask(View view) {
        this.view = view;
    }

    @Override
    protected Connection doInBackground(DatabaseAddress... params) {
        DatabaseAddress address = params[0];

        try {
            checkConnectivity();
        } catch(Exception e) {
            exception = new DatabaseException("No Network!", e);
            return null;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            exception = new DatabaseException("Driver missing!", e);
            return null;
        }

        try {
            Connection c = DriverManager.getConnection(address.getUrl(), "testuser", "123456");

            return c;
        } catch(SQLException e) {
            exception = new DatabaseException("Failed to connect to Database!", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Connection result) {
        if (result != null) {
            showMessage("Connected!");
        } else {
            if (exception != null) {
                exception.printStackTrace();
                showMessage(exception.getMessage());
            } else {
                showMessage("Unknown Error!");
            }
        }
    }

    private void showMessage(String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, 5000);
        snackbar.show();
    }

    private void checkConnectivity() throws IllegalStateException {
        ConnectivityManager cm = (ConnectivityManager) view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new IllegalStateException("No Network!");
        }
    }

    public Exception getException() {
        return exception;
    }

}
