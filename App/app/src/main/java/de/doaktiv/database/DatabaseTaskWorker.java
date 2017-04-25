package de.doaktiv.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.doaktiv.android.DatabaseService;

public class DatabaseTaskWorker implements Runnable {

    private final DatabaseService service;
    private final Thread workerThread;
    private boolean stopRequested = false;
    private final Object waitLock = new Object();
    private final List<DatabaseTask> taskQueue = new ArrayList<>();
    private final Map<DatabaseTask, DatabaseTaskObserver> taskToObserverMap = new HashMap<>();

    public DatabaseTaskWorker(DatabaseService service) {
        this.service = service;

        this.workerThread = new Thread(this, "DatabaseTaskWorker-Thread");
        this.workerThread.start();
    }

    public void execute(DatabaseTask task, DatabaseTaskObserver observer) {
        if (task != null) {
            taskQueue.add(task);

            if (observer != null) {
                taskToObserverMap.put(task, observer);
            }

            synchronized (waitLock) {
                waitLock.notifyAll();
            }
        }
    }

    public void stop() {
        this.stopRequested = true;

        synchronized (waitLock) {
            waitLock.notifyAll();
        }
    }

    @Override
    public void run() {
        while (!stopRequested) {
            if (taskQueue.isEmpty()) {
                synchronized (waitLock) {
                    try {
                        waitLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (!taskQueue.isEmpty()) {
                DatabaseTask task = taskQueue.remove(0);
                DatabaseTaskObserver observer = taskToObserverMap.remove(task);

                Object result = task.execute(service);

                if (observer != null) {
                    observer.onFinish(result);
                }
            }
        }
    }
}
