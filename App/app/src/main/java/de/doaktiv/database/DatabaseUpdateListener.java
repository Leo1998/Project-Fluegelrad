package de.doaktiv.database;

public interface DatabaseUpdateListener {

    /**
     * called when any data in the Database changed
     */
    void onDatabaseChanged();

}
