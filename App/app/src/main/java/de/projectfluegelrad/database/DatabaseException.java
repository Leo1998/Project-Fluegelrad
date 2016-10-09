package de.projectfluegelrad.database;

public class DatabaseException extends Exception {

    public DatabaseException(String detailMessage) {
        super(detailMessage);
    }

    public DatabaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DatabaseException(Throwable throwable) {
        super(throwable);
    }

    public DatabaseException() {
        super();
    }
}
