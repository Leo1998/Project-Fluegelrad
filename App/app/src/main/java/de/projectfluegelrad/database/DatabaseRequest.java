package de.projectfluegelrad.database;

public enum DatabaseRequest {

    /**
     * request to fetch the latest data from the server
     */
    RefreshEventList,

    /**
     * request to save the database to device storage
     */
    SaveEventList

}
