package de.projectfluegelrad.database;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Date;

public class Event implements Parcelable {

    protected Event(Parcel in) {
        id = in.readInt();
        location = in.readString();
        category = in.readString();
        price = in.readInt();
        host = in.readString();
        date = new Date(in.readLong());
        description = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public static Event readEvent(InputStream in) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String json = "";
        while((json += reader.readLine()) != null);
        reader.close();

        JSONObject obj = new JSONObject(new JSONTokener(json));

        Event event = new Event(obj.getInt("id"), obj.getString("location"), obj.getString("category"), obj.getInt("price"), obj.getString("host"), new Date(obj.getLong("date")), obj.getString("description"));

        return event;
    }

    public static void writeEvent(OutputStream out, Event event) throws IOException, JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", event.getId());
        obj.put("location", event.getLocation());
        obj.put("category", event.getCategory());
        obj.put("price", event.getPrice());
        obj.put("host", event.getHost());
        obj.put("date", event.getDate().getTime());
        obj.put("description", event.getDescription());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

        writer.write(obj.toString());
        writer.close();
    }

    private int id;
    private String location;
    private String category;
    private int price;
    private String host;
    private Date date;
    private String description;

    public Event(int id, String location, String category, int price, String host, Date date, String description) {
        this.id = id;
        this.location = location;
        this.category = category;
        this.price = price;
        this.host = host;
        this.date = date;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public int getPrice() {
        return price;
    }

    public String getHost() {
        return host;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", host='" + host + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != event.id) return false;
        if (price != event.price) return false;
        if (!location.equals(event.location)) return false;
        if (!category.equals(event.category)) return false;
        if (!host.equals(event.host)) return false;
        if (!date.equals(event.date)) return false;
        return description.equals(event.description);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(location);
        parcel.writeString(category);
        parcel.writeInt(price);
        parcel.writeString(host);
        parcel.writeLong(date.getTime());
        parcel.writeString(description);
    }
}
