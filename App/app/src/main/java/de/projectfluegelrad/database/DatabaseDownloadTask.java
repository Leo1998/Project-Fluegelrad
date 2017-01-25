package de.projectfluegelrad.database;

public class DatabaseDownloadTask implements DatabaseTask<Void, Void> {

    @Override
    public Void execute(DatabaseManager databaseManager, Void... params) {
        try {
            String json = databaseManager.executeScript("http://fluegelrad.ddns.net/recieveDatabase.php", null);

            databaseManager.readDatabase(json, true);
        } catch(Exception e) {
            databaseManager.getLogger().log(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
