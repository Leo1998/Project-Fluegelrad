package de.projectfluegelrad.database;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event implements Parcelable {

    protected Event(Parcel in) {
        id = in.readInt();
        location = in.readString();
        category = in.readString();
        price = in.readInt();
        host = in.readString();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(in.readLong()));
        date = c;
        description = in.readString();
        maxParticipants = in.readInt();
        participants = in.readInt();
        age = in.readInt();
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

    public static Event readEvent(JSONObject obj) throws JSONException, ParseException {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        c.setTime(simpleDateFormat.parse(obj.getString("date")));

        Event event = new Event(obj.getInt("id"), obj.getString("location"), obj.getString("category"), obj.getInt("price"), obj.getString("host"), c, obj.getString("description"), obj.getInt("maxParticipants"), obj.getInt("participants"), obj.getInt("age"));

        return event;
    }

    public static JSONObject writeEvent(Event event) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", event.getId());
        obj.put("location", event.getLocation());
        obj.put("category", event.getCategory());
        obj.put("price", event.getPrice());
        obj.put("host", event.getHost());
        obj.put("date", event.getDateFormatted());
        obj.put("description", event.getDescription());
        obj.put("maxParticipants", event.getMaxParticipants());
        obj.put("participants", event.getParticipants());
        obj.put("age", event.getAge());

        return obj;
    }

    private int id;
    private String location;
    private String category;
    private int price;
    private String host;
    private Calendar date;
    private String description;
    private int maxParticipants;
    private int participants;
    private int age;

    public Event(int id, String location, String category, int price, String host, Calendar date, String description, int maxParticipants, int participants, int age) {
        this.id = id;
        this.location = location;
        this.category = category;
        this.price = price;
        this.host = host;
        this.date = date;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.age = age;
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

    public Calendar getDate() {
        return date;
    }

    public String getDateFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return simpleDateFormat.format(this.date.getTime());
    }

    public String getDescription() {
        return description;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getParticipants() {
        return participants;
    }

    public int getAge() {
        return age;
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
                ", maxParticipants=" + maxParticipants +
                ", participants=" + participants +
                ", age=" + age +
                '}';
    }

    public boolean equalsId(Event e) {
        return this.id == e.getId();
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
        parcel.writeLong(date.getTimeInMillis());
        parcel.writeString(description);
        parcel.writeInt(maxParticipants);
        parcel.writeInt(participants);
        parcel.writeInt(age);
    }
}
