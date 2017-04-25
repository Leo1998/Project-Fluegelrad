package de.doaktiv.database;

import de.doaktiv.android.DatabaseService;

/**
 * representation of a task that interacts with the database
 *
 * @param <Param>
 * @param <Result>
 */
public abstract class DatabaseTask<Param, Result> {

    protected final Param[] params;

    public DatabaseTask(Param... params) {
        this.params = params;
    }

    public abstract Result execute(DatabaseService service);

}
