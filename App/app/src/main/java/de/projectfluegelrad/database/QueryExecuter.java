package de.projectfluegelrad.database;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import de.projectfluegelrad.database.logging.Logger;

public final class QueryExecuter implements Runnable {

    private final Logger logger;

    private ConnectivityManager cm;

    private final DatabaseAddress address;
    private final String username;
    private final String password;

    private Connection connection;
    private ConnectionStatus connectionStatus = ConnectionStatus.NOT_CONNECTED;

    private Object executorLock = new Object();
    private Object executionLock = new Object();

    private Query nextQuery = null;
    private ResultSet result = null;

    public QueryExecuter(Logger logger, ConnectivityManager cm, DatabaseAddress address, String password, String username) {
        this.logger = logger;
        this.cm = cm;
        this.address = address;
        this.password = password;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            connectionStatus = ConnectionStatus.PENDING;

            connect();

            connectionStatus = ConnectionStatus.CONNECTED;
        } catch(DatabaseException e) {
            logger.log("Failed to Connect!");
            connectionStatus = ConnectionStatus.ERROR;
        }

        Statement statement = null;

        while(true) {
            synchronized (executorLock) {
                try {
                    executorLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (statement == null) {
                try {
                    statement = connection.createStatement();
                } catch (SQLException e) {
                    logger.log("Failed to access database!");
                }
            }

            if (nextQuery != null) {
                try {
                    result = nextQuery.execute(statement);
                } catch(SQLException e) {
                    logger.log("Failed to executeQuery!");
                }
            }

            synchronized (executionLock) {
                executionLock.notify();
            }
        }
    }

    private void connect() throws DatabaseException {
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
            connection = DriverManager.getConnection(address.getUrl("testuser", "123456"));

            if (connection == null) {
                throw new DatabaseException("Failed to connect to Database!");
            }
        } catch(SQLException e) {
            throw new DatabaseException("Failed to connect to Database!", e);
        }
    }

    public boolean connectAndWait() {
        new Thread(this, "QueryExecutor").start();

        while(connectionStatus == ConnectionStatus.PENDING || connectionStatus == ConnectionStatus.NOT_CONNECTED) {
            try {
                Thread.sleep(10);
            } catch(InterruptedException e) {}
        }

        return connectionStatus == ConnectionStatus.CONNECTED;
    }

    public synchronized ResultSet executeQuery(Query query) {
        if (connection == null) {
            throw new IllegalStateException("Not yet connected");
        }

        this.nextQuery = query;

        synchronized (executorLock) {
            executorLock.notifyAll();
        }

        synchronized (executionLock) {
            try {
                executionLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private void checkConnectivity() throws IllegalStateException {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            throw new IllegalStateException("No Network!");
        }
    }

}
