package de.projectfluegelrad.database.tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;


public abstract class DatabaseTask<Params, Result> {

    public interface Listener<Params, Result> {

        public void onPreRun(Params[] params);

        public void onPostRun(Result result);

    }

    /**
     * just a wrapper to execute the <code>run</code> method asynchronously
     */
    private AsyncTask<Params, Void, Result> asyncTask;

    private void createAsyncTask() {
        this.asyncTask = new AsyncTask<Params, Void, Result>() {
            @Override
            protected Result doInBackground(Params[] params) {
                if (listener != null)
                    listener.onPreRun(params);

                return DatabaseTask.this.run(params);
            }

            @Override
            protected void onPostExecute(Result result) {
                if (listener != null)
                    listener.onPostRun(result);
            }
        };
    }

    private Exception exception;
    private Listener listener;
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

    protected void throwException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
