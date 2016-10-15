package de.projectfluegelrad.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface Query {

    ResultSet execute(Statement statement) throws SQLException;

}
