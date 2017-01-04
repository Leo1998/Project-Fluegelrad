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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Calendar dateStart = Calendar.getInstance();
        dateStart.setTime(simpleDateFormat.parse(obj.getString("dateStart")));

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(simpleDateFormat.parse(obj.getString("dateEnd")));

        Location location = new Location(obj.getString("location.address"), obj.getDouble("location.longitude"), obj.getDouble("location.latitude"));

        Sponsor host = new Sponsor(obj.getInt("host.id"), obj.getString("host.name"), obj.getString("host.description"), obj.getString("host.image"), obj.getString("host.mail"), obj.getString("host.phone"), obj.getString("host.web"));

        Event event = new Event(obj.getInt("id"), obj.getString("name"), obj.getInt("price"), dateStart, dateEnd, obj.getString("description"), obj.getInt("maxParticipants"), obj.getInt("participants"), obj.getInt("ageMin"), obj.getInt("ageMax"), location,  host);

        return event;
    }

    public static JSONObject writeEvent(Event event) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", event.getId());
        obj.put("name", event.getName());
        obj.put("price", event.getPrice());
        obj.put("dateStart", event.getDateStartFormatted());
        obj.put("dateEnd", event.getDateEndFormatted());
        obj.put("description", event.getDescription());
        obj.put("maxParticipants", event.getMaxParticipants());
        obj.put("participants", event.getParticipants());
        obj.put("ageMin", event.getAgeMin());
        obj.put("ageMax", event.getAgeMax());

        obj.put("location.address", event.getLocation().getAddress());
        obj.put("location.longitude", event.getLocation().getLongitude());
        obj.put("location.latitude", event.getLocation().getLatitude());

        obj.put("host.id", event.getHost().getId());
        obj.put("host.name", event.getHost().getName());
        obj.put("host.description", event.getHost().getDescription());
        obj.put("host.image", event.getHost().getImagePath());
        obj.put("host.mail", event.getHost().getMail());
        obj.put("host.phone", event.getHost().getPhone());
        obj.put("host.web", event.getHost().getWeb());

        return obj;
    }

    private int id;
    private String name;
    private int price;
    private Calendar dateStart;
    private Calendar dateEnd;
    private String description;
    private int maxParticipants;
    private int participants;
    private int ageMin;
    private int ageMax;
    private Location location;
    private Sponsor host;

    public Event(int id, String name,  int price, Calendar dateStart, Calendar dateEnd, String description, int maxParticipants, int participants, int ageMin, int ageMax, Location location, Sponsor host) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.participants = participants;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.location = location;
        this.host = host;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
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

    public Location getLocation() {
        return location;
    }

    public Sponsor getHost() {
        return host;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", dateStart=" + getDateStartFormatted() +
                ", dateEnd=" + getDateEndFormatted() +
                ", description='" + description + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", participants=" + participants +
                ", ageMin=" + ageMin +
                ", ageMax=" + ageMax +
                ", location=" + location +
                ", host=" + host +
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
