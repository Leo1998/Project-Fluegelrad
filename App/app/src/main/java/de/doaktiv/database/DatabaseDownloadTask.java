package de.doaktiv.database;

/**
 * Downloads all data from the server
 */
public class DatabaseDownloadTask implements DatabaseTask<Void, Void> {

    private static final String TAG = "DatabaseDownloadTask";

    @Override
    public Void execute(DatabaseManager databaseManager, Void... params) {
        try {
            String json = databaseManager.executeScript("http://fluegelrad.ddns.net/scripts/getEvents.php", null);

            databaseManager.getDatabase().readDatabase(json);
            databaseManager.saveDatabaseToStorage();

            if (databaseManager.getReceiver() != null)
                databaseManager.getReceiver().onReceive(databaseManager.getDatabase());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
