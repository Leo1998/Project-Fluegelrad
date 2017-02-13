package de.doaktiv.database;

/**
 * representation of a task that interacts with the database
 *
 * @param <Param>
 * @param <Result>
 */
public interface DatabaseTask<Param, Result> {

    Result execute(DatabaseManager databaseManager, Param... params);

}
