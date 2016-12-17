package de.projectfluegelrad.database;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class Image {

    public static Image readImage(JSONObject obj) throws JSONException, ParseException {
        Image image = new Image(obj.getInt("id"), obj.getString("path"), obj.getInt("eventId"), obj.getString("description"));

        return image;
    }

    public static JSONObject writeImage(Image image) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", image.getId());
        obj.put("path", image.getPath());
        obj.put("eventId", image.getEventId());
        obj.put("description", image.getDescription());

        return obj;
    }

    private int id;
    private String path;
    private int eventId;
    private String description;

    public Image(int id, String path, int eventId, String description) {
        this.id = id;
        this.path = path;
        this.eventId = eventId;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public int getEventId() {
        return eventId;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", eventId=" + eventId +
                ", description='" + description + '\'' +
                '}';
    }
}
