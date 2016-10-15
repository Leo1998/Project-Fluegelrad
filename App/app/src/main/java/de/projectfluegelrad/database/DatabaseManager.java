package de.projectfluegelrad.database;

import android.view.View;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DatabaseManager {

    private View attachedView;

    private QueryExecuter queryExecuter;

    public DatabaseManager(View attachedView) {
        this.attachedView = attachedView;

        this.queryExecuter = new QueryExecuter(attachedView, new DatabaseAddress("pipigift.ddns.net", 3306, "fluegelrad"), "testuser", "123456");

        if (this.queryExecuter.connectAndWait()) {
            ResultSet result = this.queryExecuter.executeQuery(new BasicQuery("SELECT * FROM events;"));
            dumpResultSet(result);
        }
    }

    private void dumpResultSet(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsNumber = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + metaData.getColumnName(i));
                }
                System.out.println("");
            }
        } catch(SQLException e) {

        }
    }

}
