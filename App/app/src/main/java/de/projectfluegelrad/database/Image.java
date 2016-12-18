package de.projectfluegelrad.database;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

public class Image implements Parcelable{

    protected Image(Parcel in) {
        id = in.readInt();
        path = in.readString();
        eventId = in.readInt();
        description = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeInt(eventId);
        dest.writeString(description);
        dest.writeParcelable(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

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

        this.loadBitmap();
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

    public void loadBitmap() {
        try {
            URL url = new URL("http://fluegelrad.ddns.net/" + this.path);
            URLConnection c = url.openConnection();

            InputStream in = c.getInputStream();

            this.bitmap = BitmapFactory.decodeStream(in);
            System.out.println(toString() + " was loaded!");

            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
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
