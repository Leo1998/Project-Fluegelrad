package de.projectfluegelrad.database;

import java.sql.*;

public class DatabaseManager {

    private Connection connect = null;

    public DatabaseManager() {
        try {
            connectToDatabase();
        } catch(DatabaseException e) {
            e.printStackTrace();//TODO
        }
    }

    private void connectToDatabase() throws DatabaseException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Driver missing!", e);
        }

        try {
            connect = DriverManager.getConnection("jdbc:mysql://pipigift.ddns.net/fluegelrad?user=testuser&password=123456");
        } catch(SQLException e) {
            throw new DatabaseException("Failed to connect to Database!", e);
        }
    }

}
