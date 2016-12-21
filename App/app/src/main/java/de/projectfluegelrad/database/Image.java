package de.projectfluegelrad.database;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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

    private Bitmap bitmap;

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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void load(File cacheDir) throws IOException {
        File cachedFile = new File(cacheDir, "image#" + this.id);

        if (!cachedFile.exists()) {
            URL url = new URL("http://fluegelrad.ddns.net/" + this.path);
            URLConnection c = url.openConnection();

            InputStream in = c.getInputStream();
            OutputStream out = new FileOutputStream(cachedFile);

            StreamUtils.copyStream(in, out);
            in.close();
            out.close();
        }

        InputStream in = new FileInputStream(cachedFile);

        Image.this.bitmap = BitmapFactory.decodeStream(in);

        in.close();
    }

    public void disposeBitmap() {
        bitmap = null;
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
