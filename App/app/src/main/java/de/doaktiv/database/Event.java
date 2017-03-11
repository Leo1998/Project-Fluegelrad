package de.doaktiv.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * a class to store all event data
 */
public class Event {

    public static Event readEvent(JSONObject obj) throws JSONException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Calendar dateStart = Calendar.getInstance();
        dateStart.setTime(simpleDateFormat.parse(obj.getString("dateStart")));

        Calendar dateEnd = Calendar.getInstance();
        dateEnd.setTime(simpleDateFormat.parse(obj.getString("dateEnd")));

        Location location = new Location(obj.getString("location.address"), obj.getDouble("location.longitude"), obj.getDouble("location.latitude"));

        JSONArray sponsorsArray = obj.getJSONArray("sponsors");
        int[] sponsors = new int[sponsorsArray.length()];
        for (int i = 0; i < sponsorsArray.length(); i++) {
            sponsors[i] = sponsorsArray.getJSONObject(i).getInt("sponsorId");
        }

        List<Image> images = new ArrayList<>();
        JSONArray imageArray = obj.getJSONArray("images");
        for (int i = 0; i < imageArray.length(); i++) {
            JSONObject imageObj = imageArray.getJSONObject(i);

            Image image = Image.readImage(imageObj);

            //Log.i("DatabaseManager", "Image: " + image.toString());

            images.add(image);
        }

        Event event = new Event(obj.getInt("id"), obj.getString("name"), obj.getInt("price"), dateStart, dateEnd, obj.getString("description"), obj.getInt("maxParticipants"), obj.getInt("participants"), obj.getInt("ageMin"), obj.getInt("ageMax"), location, obj.getInt("hostId"), sponsors, images);

        if (obj.has("participating")) {
            event.participating = obj.getBoolean("participating");
        }
        if (obj.has("userRating")) {
            event.userRating = obj.getInt("userRating");
        }

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
        obj.put("hostId", event.getHostId());

        JSONArray sponsorsArray = new JSONArray();
        for (int i = 0; i < event.getSponsors().length; i++) {
            sponsorsArray.put(event.getSponsors()[i]);
        }
        obj.put("sponsors", sponsorsArray);

        obj.put("location.address", event.getLocation().getAddress());
        obj.put("location.longitude", event.getLocation().getLongitude());
        obj.put("location.latitude", event.getLocation().getLatitude());

        JSONArray imagesArray = new JSONArray();
        for (int i = 0; i < event.getImages().size(); i++) {
            imagesArray.put(Image.writeImage(event.getImages().get(i)));
        }
        obj.put("images", imagesArray);

        obj.put("participating", event.isParticipating());
        obj.put("userRating", event.getUserRating());

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
    private int hostId;
    private int[] sponsors;
    private List<Image> images;

    // local values are not in the database but are saved locally

    /**
     * local value
     */
    private boolean participating;
    /**
     * local value
     */
    private int userRating;

    /**
     * Constructor...
     *
     * @param id
     * @param name
     * @param price
     * @param dateStart
     * @param dateEnd
     * @param description
     * @param maxParticipants
     * @param participants
     * @param ageMin
     * @param ageMax
     * @param location
     * @param hostId
     * @param sponsors
     * @param images
     */
    public Event(int id, String name, int price, Calendar dateStart, Calendar dateEnd, String description, int maxParticipants, int participants, int ageMin, int ageMax, Location location, int hostId, int[] sponsors, List<Image> images) {
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
        this.hostId = hostId;
        this.sponsors = sponsors;
        this.images = images;
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

    /**
     * @return dateStart formatted as a string
     */
    public String getDateStartFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return simpleDateFormat.format(this.dateStart.getTime());
    }

    public Calendar getDateEnd() {
        return dateEnd;
    }

    /**
     * @return dateEnd formatted as a string
     */
    public String getDateEndFormatted() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return simpleDateFormat.format(this.dateEnd.getTime());
    }

    /**
     * @return the duration of the event formatted as a string
     */
    public String getDurationFormatted() {
        Calendar duration = Calendar.getInstance();
        duration.setTimeInMillis(dateEnd.getTimeInMillis() - dateStart.getTimeInMillis());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        return simpleDateFormat.format(duration.getTime());
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

    public int getHostId() {
        return hostId;
    }

    public int[] getSponsors() {
        return sponsors;
    }

    public List<Image> getImages() {
        return images;
    }

    public boolean isParticipating() {
        return participating;
    }

    public int getUserRating() {
        return userRating;
    }

    /**
     * used to participate with the event (this method is only local and should only be called from {@link DatabaseParticipateTask})
     *
     * @param fakeIncrement if true the number of participants gets incremented locally so it looks right until the database gets refreshed
     */
    public void participate(boolean fakeIncrement) {
        if (isParticipating())
            throw new IllegalStateException("Already Participating!");

        this.participating = true;
        this.participants++;
    }

    /**
     * used to rate an event (this method is only local and should only be called from {@link DatabaseRateTask})
     *
     * @param rating the rating
     */
    public void rate(int rating) {
        this.userRating = rating;
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
                ", hostId=" + hostId +
                ", sponsors=" + Arrays.toString(sponsors) +
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
