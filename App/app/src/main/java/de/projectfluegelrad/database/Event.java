package de.projectfluegelrad.database;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Event {

    public static Event readEvent(JSONObject obj) throws JSONException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

        Calendar cStart = Calendar.getInstance();
        cStart.setTime(simpleDateFormat.parse(obj.getString("dateStart")));

        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(simpleDateFormat.parse(obj.getString("dateEnd")));

        Event event = new Event(obj.getInt("id"), obj.getString("name"), new Location(obj.getString("address"), obj.getDouble("longitude"), obj.getDouble("latitude")), obj.getInt("price"), obj.getInt("hostId"), cStart, cEnd, obj.getString("description"), obj.getInt("maxParticipants"), obj.getInt("participants"), obj.getInt("ageMin"), obj.getInt("ageMax"));

        return event;
    }

    public static JSONObject writeEvent(Event event) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", event.getId());
        obj.put("name", event.getName());
        obj.put("address", event.getLocation().getAddress());
        obj.put("longitude", event.getLocation().getLongitude());
        obj.put("latitude", event.getLocation().getLatitude());
        obj.put("price", event.getPrice());
        obj.put("hostId", event.getHost());
        obj.put("dateStart", event.getDateStartFormatted());
        obj.put("dateEnd", event.getDateEndFormatted());
        obj.put("description", event.getDescription());
        obj.put("maxParticipants", event.getMaxParticipants());
        obj.put("participants", event.getParticipants());
        obj.put("ageMin", event.getAgeMin());
        obj.put("ageMax", event.getAgeMax());

        return obj;
    }

    private int id;
    private String name;
    private Location location;
    private int price;
    private int host;
    private Calendar dateStart;
    private Calendar dateEnd;
    private String description;
    private int maxParticipants;
    private int participants;
    private int ageMin;
    private int ageMax;

    public Event(int id, String name, Location location, int price, int host, Calendar dateStart, Calendar dateEnd, String description, int maxParticipants, int participants, int ageMin, int ageMax) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.host = host;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public int getHost() {
        return host;
    }

    public Calendar getDateStart() {
        return dateStart;
    }

    public String getDateStartFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return simpleDateFormat.format(this.dateStart.getTime());
    }

    public Calendar getDateEnd() {
        return dateEnd;
    }

    public String getDateEndFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return simpleDateFormat.format(this.dateEnd.getTime());
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

    public int getAgeMin() {
        return ageMin;
    }

    public int getAgeMax() {
        return ageMax;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name=" + name +
                ", location=" + location +
                ", price=" + price +
                ", host=" + host +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                ", description='" + description + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", participants=" + participants +
                ", ageMin=" + ageMin +
                ", ageMax=" + ageMax +
                '}';
    }

    public boolean equalsId(Event e) {
        return this.id == e.getId();
    }

    @Override
    public int hashCode() {
        return id;
    }
}
