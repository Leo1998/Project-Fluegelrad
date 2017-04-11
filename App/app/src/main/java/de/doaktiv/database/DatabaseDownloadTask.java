package de.doaktiv.database;

/**
 * Downloads all data from the server
 */
public class DatabaseDownloadTask implements DatabaseTask<Void, Void> {

    @Override
    public Void execute(DatabaseManager databaseManager, Void... params) {
        try {
            String json = databaseManager.executeScript("http://fluegelrad.ddns.net/scripts/getEvents.php", null);

            databaseManager.readDatabase(json, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
