package de.projectfluegelrad.database;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.projectfluegelrad.database.logging.Logger;

public final class QueryExecuter {

    private final Logger logger;

    private ConnectivityManager cm;

    private final DatabaseAddress address;
    private final String username;
    private final String password;

    private Connection connection;
    private ConnectionStatus connectionStatus = ConnectionStatus.NOT_CONNECTED;
    private Statement statement = null;

    public QueryExecuter(Logger logger, ConnectivityManager cm, DatabaseAddress address, String username, String password) {
        this.logger = logger;
        this.cm = cm;
        this.address = address;
        this.username = username;
        this.password = password;
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
            throw new DatabaseException("No Network!", e);
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new DatabaseException("Driver missing!", e);
        }

        try {
            connection = DriverManager.getConnection(address.getUrl(username, password));

            if (connection == null) {
                throw new DatabaseException("Failed to connect to Database!");
            }
        } catch(SQLException e) {
            throw new DatabaseException("Failed to connect to Database!", e);
        }
    }

    public synchronized ResultSet executeQuery(Query query) {
        if (connection == null) {
            throw new IllegalStateException("Not yet connected");
        }

        if (statement == null) {
            try {
                statement = connection.createStatement();
            } catch (SQLException e) {
                logger.log("Failed to access database!");
            }
        }

        try {
            return query.execute(statement);
        } catch(SQLException e) {
            logger.log("Failed to execute Query!");
        }

        return null;
    }

    private void checkConnectivity() throws IllegalStateException {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new IllegalStateException("No Network!");
        }
    }

}
