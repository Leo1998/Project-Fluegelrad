package de.projectfluegelrad.database;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.projectfluegelrad.R;
import de.projectfluegelrad.database.logging.Logger;

public final class QueryExecuter {

    private final Logger logger;

    private ConnectivityManager cm;

    private final DatabaseAddress address;
    private final String username;
    private final String password;

    private Context context;

    private Connection connection;
    private ConnectionStatus connectionStatus = ConnectionStatus.NOT_CONNECTED;
    private Statement statement = null;

    public QueryExecuter(Context context, Logger logger, ConnectivityManager cm, DatabaseAddress address, String username, String password) {
        this.logger = logger;
        this.cm = cm;
        this.address = address;
        this.username = username;
        this.password = password;
        this.context = context;
    }

    public boolean connect() {
        try {
            connectionStatus = ConnectionStatus.PENDING;

            connectInternal();

            connectionStatus = ConnectionStatus.CONNECTED;
        } catch(DatabaseException e) {
            e.printStackTrace();
            logger.log(e.getMessage());
            connectionStatus = ConnectionStatus.ERROR;
        }

        return connectionStatus == ConnectionStatus.CONNECTED;
    }

    private void connectInternal() throws DatabaseException {
        try {
            checkConnectivity();
        } catch(Exception e) {
            throw new DatabaseException(context.getResources().getText(R.string.network_failure).toString(),e);
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new DatabaseException(context.getResources().getText(R.string.missing_driver_failure).toString(), e);
        }

        try {
            connection = DriverManager.getConnection(address.getUrl(username, password));

            if (connection == null) {
                throw new DatabaseException(context.getResources().getText(R.string.database_connction_failure).toString());
            }
        } catch(SQLException e) {
            throw new DatabaseException(context.getResources().getText(R.string.database_connction_failure).toString(), e);
        }
    }

    public synchronized ResultSet executeQuery(Query query) {
        if (connection == null) {
            throw new IllegalStateException(context.getResources().getText(R.string.no_connction_failure).toString());
        }

        if (statement == null) {
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                logger.log(context.getResources().getText(R.string.database_access_failure).toString());
            }
        }

        try {
            return query.execute(statement);
        } catch(SQLException e) {
            logger.log(context.getResources().getText(R.string.query_execution_failure).toString());
        }

        return null;
    }

    private void checkConnectivity() throws IllegalStateException {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new IllegalStateException(context.getResources().getText(R.string.network_failure).toString());
        }
    }

}
