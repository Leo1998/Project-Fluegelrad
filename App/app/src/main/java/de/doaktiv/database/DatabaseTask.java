package de.doaktiv.database;

public interface DatabaseTask<Param, Result> {

    Result execute(DatabaseManager databaseManager, Param... params);

}
