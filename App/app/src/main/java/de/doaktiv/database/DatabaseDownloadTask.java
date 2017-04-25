package de.doaktiv.database;

import de.doaktiv.android.DatabaseService;

/**
 * Downloads all data from the server
 */
public class DatabaseDownloadTask extends DatabaseTask<Void, Void> {

    private static final String TAG = "DatabaseDownloadTask";

    @Override
    public Void execute(DatabaseService service) {
        try {
            String json = service.executeScript("http://fluegelrad.ddns.net/scripts/getEvents.php", null);

            service.getDatabase().readDatabase(json);
            service.saveDatabaseToStorage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
