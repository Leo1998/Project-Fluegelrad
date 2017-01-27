package de.projectfluegelrad.database;

import android.util.Log;

public class DatabaseDownloadTask implements DatabaseTask<Void, Void> {

    @Override
    public Void execute(DatabaseManager databaseManager, Void... params) {
        try {
            String json = databaseManager.executeScript("http://fluegelrad.ddns.net/scripts/getEvents.php", null);

            databaseManager.readDatabase(json, true);
        } catch(Exception e) {
            databaseManager.getLogger().log(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
