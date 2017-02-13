package de.doaktiv.database;

/**
 * the local representation of a user.
 */
public class User {

    /**
     * the user's id
     */
    private int id;
    /**
     * the hashed security token
     */
    private String hashedToken;

    /**
     * Constructor.
     *
     * @param id
     * @param hashedToken
     */
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", hashedToken='" + hashedToken + '\'' +
                '}';
    }
}
