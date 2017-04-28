package de.doaktiv.database;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Database {

    private static final String TAG = "Database";

    /**
     * list of all events
     */
    private final List<Event> eventList = new ArrayList<Event>();
    /**
     * list of all sponsors
     */
    private final List<Sponsor> sponsorList = new ArrayList<Sponsor>();

    public Database(File databaseFile) {
        if (databaseFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(databaseFile)));

                String json = "";
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    json += line;
                }
                reader.close();

                readDatabase(json);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "read " + eventList.size() + " events and " + sponsorList.size() + " sponsors from storage!");
    }

    /**
     * called when a new event is registered
     *
     * @param event
     */
    private void registerEvent(Event event) {
        for (int i = 0; i < eventList.size(); i++) {
            Event currentEvent = eventList.get(i);

            if (currentEvent.equalsId(event)) {
                if (currentEvent == event) {
                    // already exists
                    return;
                } else {
                    // update
                    eventList.remove(currentEvent);

                    if (currentEvent.isParticipating()) {
                        event.participate(false);
                    }

                    break;
                }
            }
        }

        eventList.add(event);
    }

    /**
     * called when a new sponsor is registered
     *
     * @param sponsor
     */
    private void registerSponsor(Sponsor sponsor) {
        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor currentSponsor = sponsorList.get(i);

            if (currentSponsor.equalsId(sponsor)) {
                if (currentSponsor.equals(sponsor)) {
                    // already exists
                    return;
                } else {
                    // update
                    sponsorList.remove(currentSponsor);
                    break;
                }
            }
        }

        sponsorList.add(sponsor);
    }

    /**
     * sorts the eventList by dateStart
     */
    private void sortEvents() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return e1.getDateStart().compareTo(e2.getDateStart());
            }
        });
    }

    public List<Event> getEventList() {
        return Collections.unmodifiableList(eventList);
    }

    public List<Sponsor> getSponsorList() {
        return Collections.unmodifiableList(sponsorList);
    }

    public int getEventCount() {
        return eventList.size();
    }

    public int getSponsorCount() {
        return sponsorList.size();
    }

    public Event getEvent(int eventId) {
        for (int i = 0; i < eventList.size(); i++) {
            Event event = eventList.get(i);

            if (event.getId() == eventId) {
                return event;
            }
        }

        return null;
    }

    public List<Event> getEvents(int[] ids) {
        final List<Event> events = new ArrayList<>();

        for (Event e : eventList) {
            for (int i = 0; i < ids.length; i++) {
                int id = ids[i];

                if (e.getId() == id) {
                    events.add(e);
                }
            }
        }

        return events;
    }

    public Sponsor getSponsor(int sponsorId) {
        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor sponsor = sponsorList.get(i);

            if (sponsor.getId() == sponsorId) {
                return sponsor;
            }
        }

        return null;
    }

    public List<Sponsor> getSponsors(Event event) {
        int[] sponsorIds = event.getSponsors();
        List<Sponsor> sponsors = new ArrayList<>();

        for (int i = 0; i < sponsorList.size(); i++) {
            Sponsor sponsor = sponsorList.get(i);

            for (int j = 0; j < sponsorIds.length; j++) {
                if (sponsor.getId() == sponsorIds[j]) {
                    sponsors.add(sponsor);
                }
            }
        }

        return sponsors;
    }

    /**
     * helper method for reading the Database from json text
     *
     * @param json
     * @param save
     * @throws JSONException
     * @throws ParseException
     */
    void readDatabase(String json) throws JSONException, ParseException {
        JSONObject root = new JSONObject(new JSONTokener(json));

        JSONArray eventDataArray = root.getJSONArray("events");
        JSONArray sponsorDataArray = root.getJSONArray("sponsors");

        for (int i = 0; i < eventDataArray.length(); i++) {
            JSONObject obj = eventDataArray.getJSONObject(i);

            Event event = Event.readEvent(obj);

            registerEvent(event);
        }

        for (int i = 0; i < sponsorDataArray.length(); i++) {
            JSONObject obj = sponsorDataArray.getJSONObject(i);

            Sponsor sponsor = Sponsor.readSponsor(obj);

            registerSponsor(sponsor);
        }

        sortEvents();
    }

    public List<Event> getRecentEventList() {
        List<Event> list = new ArrayList<>();

        for (int i = eventList.size() - 1; i >= 0; i--) {
            if (eventList.get(i).getDateEnd().compareTo(Calendar.getInstance()) > 0) {
                list.add(eventList.get(i));
            } else {
                break;
            }
        }

        Collections.reverse(list);

        return list;
    }

    public List<Event> getHomeEventList() {//TODO
        List<Event> recent = getRecentEventList();
        List<Event> shown = new ArrayList<>();

        for (int i = 0; i < 1; i++) {//wtf macht ihr?
            if (recent.size() >= 1) {
                shown.add(recent.get(0));
                recent.remove(0);
            }

            if (recent.size() >= 1) {
                shown.add(recent.get(0));
                recent.remove(0);
            }
        }

        for (int i = 0; i < 1; i++) {
            if (recent.size() >= 1) {
                Event mostPopular = recent.get(0);

                for (Event event : recent) {
                    if (event.getParticipants() > mostPopular.getParticipants()) {
                        mostPopular = event;
                    }
                }

                shown.add(mostPopular);
                recent.remove(mostPopular);
            }
        }

        return shown;
    }

}
