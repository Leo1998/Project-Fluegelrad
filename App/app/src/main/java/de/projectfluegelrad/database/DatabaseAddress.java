package de.projectfluegelrad.database;

public class DatabaseAddress {

    private String host;
    private int port;
    private String databaseName;

    public DatabaseAddress(String host, int port, String databaseName) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
    }

    public String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
