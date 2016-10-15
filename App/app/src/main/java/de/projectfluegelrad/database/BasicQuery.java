package de.projectfluegelrad.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BasicQuery implements Query {

    private String query;

    public BasicQuery(String query) {
        this.query = query;
    }


    @Override
    public ResultSet execute(Statement statement) throws SQLException {
        statement.execute(this.query);

        return statement.getResultSet();
    }
}
