package de.doaktiv.database;

import java.util.HashMap;
import java.util.Map;

import de.doaktiv.android.DatabaseService;

/**
 * requests participation for a given event
 */
public class DatabaseParticipateTask extends DatabaseTask<Event, Boolean> {

    @Override
    public Boolean execute(DatabaseService service) {
        try {
            Event event = params[0];

            Map<String, String> args = new HashMap<>();
            args.put("k", Integer.toString(event.getId()));

            String result = service.executeScript("http://fluegelrad.ddns.net/scripts/participate.php", args);

            event.participate(true);

            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
