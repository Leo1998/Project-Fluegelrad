package de.doaktiv.database;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import de.doaktiv.android.DatabaseService;

public class DatabaseRateTask extends DatabaseTask<DatabaseRateTask.RateParamsWrapper, Boolean> {

    private static final String TAG = "DatabaseRateTask";

    public static class RateParamsWrapper {
        public final Event event;
        public final int rating;

        public RateParamsWrapper(Event event, int rating) {
            this.event = event;
            this.rating = rating;
        }
    }

    public DatabaseRateTask(RateParamsWrapper... rateParamsWrappers) {
        super(rateParamsWrappers);
    }

    @Override
    public Boolean execute(DatabaseService service) {
        try {
            RateParamsWrapper wrapper = params[0];

            Map<String, String> args = new HashMap<>();
            args.put("k", Integer.toString(wrapper.event.getId()));
            args.put("r", Integer.toString(wrapper.rating));

            String result = service.executeScript("http://fluegelrad.ddns.net/scripts/rate.php", args);

            Log.i(TAG, result);

            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }
}
