package de.projectfluegelrad.database;

public class User {

    private int id;
    private String hashedToken;

    public User(int id, String hashedToken) {
        this.id = id;
        this.hashedToken = hashedToken;
    }

    public String getHashedToken() {
        return hashedToken;
    }

    public int getId() {
        return id;
    }

    public void setNewToken(String newToken) {
        this.hashedToken = newToken;
    }

}
