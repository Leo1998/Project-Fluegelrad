package de.doaktiv.database;

/**
 * simple interface to notify when the database somehow changed
 */
public interface DatabaseUpdateListener {

    /**
     * called when any data in the Database changed
     */
    void onDatabaseChanged();

}
