package de.projectfluegelrad.database;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;


public abstract class DatabaseTask<Params, Result> {

    /**
     * just a wrapper to execute the <code>run</code> method asynchronously
     */
    private AsyncTask<Params, Void, Result> asyncTask;

    private void createAsyncTask() {
        this.asyncTask = new AsyncTask<Params, Void, Result>() {
            @Override
            protected Result doInBackground(Params[] params) {
                return DatabaseTask.this.run(params);
            }
        };
    }

    private Exception exception;
    protected View view;

    public DatabaseTask(View view) {
        createAsyncTask();

        this.view = view;
    }

    public void execute(Params... params) {
        AsyncTask.Status s = asyncTask.getStatus();

        if (s != AsyncTask.Status.PENDING) {
            switch (s) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task: the task is already running.");
                case FINISHED:
                    createAsyncTask();
            }
        }

        asyncTask.execute(params);
    }

    protected abstract Result run(Params[] params);

    protected void showMessage(String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, 3000);
        snackbar.show();
    }

    protected void throwException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

}
