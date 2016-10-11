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

public class ConnectorTask extends ConnectorTaskParent {

    private View view;

    public ConnectorTask(View view) {
        this.view = view;
    }

    @Override
    protected Connection doInBackground(DatabaseAddress... params) {
        super.doInBackground(DatabaseAddress... params)
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
}
