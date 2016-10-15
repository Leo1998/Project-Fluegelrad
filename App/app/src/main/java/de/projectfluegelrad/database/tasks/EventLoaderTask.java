package de.projectfluegelrad.database.tasks;

import android.view.View;

import java.sql.Connection;

public class EventLoaderTask extends DatabaseTask<Connection, Void> {

    public EventLoaderTask(View view) {
        super(view);
    }

    @Override
    protected Void run(Connection[] params) {
        if (params == null || params.length != 1) {
            throwException(new IllegalArgumentException());
        }
        Connection connection = params[0];

        return null;
    }
}
