package de.projectfluegelrad.database;

public interface DatabaseTask<Result> {

    Result execute(DatabaseManager databaseManager);

}
