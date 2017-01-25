package de.projectfluegelrad.database;

import java.util.HashMap;
import java.util.Map;

public class DatabaseParticipateTask implements DatabaseTask<Event, Boolean> {

    @Override
    public Boolean execute(DatabaseManager databaseManager, Event... params) {
        try {
            Event event = params[0];

            Map<String, String> args = new HashMap<>();
            args.put("k", Integer.toString(event.getId()));

            String result = databaseManager.executeScript("http://fluegelrad.ddns.net/sendDatabase.php", args);

            event.participate(true);

            return Boolean.TRUE;
        } catch(Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
