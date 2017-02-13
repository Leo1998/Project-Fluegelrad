package de.doaktiv.database;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * a class to store Sponsor data
 */
public class Sponsor {

    public static Sponsor readSponsor(JSONObject obj) throws JSONException, ParseException {
        Sponsor sponsor = new Sponsor(obj.getInt("id"), obj.getString("name"), obj.getString("description"), obj.getString("imagePath"), obj.getString("mail"), obj.getString("phone"), obj.getString("web"));

        return sponsor;
    }

    public static JSONObject writeSponsor(Sponsor sponsor) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", sponsor.getId());
        obj.put("name", sponsor.getName());
        obj.put("description", sponsor.getDescription());
        obj.put("imagePath", sponsor.getImagePath());
        obj.put("mail", sponsor.getMail());
        obj.put("phone", sponsor.getPhone());
        obj.put("web", sponsor.getWeb());

        return obj;
    }

    private int id;
    private String name;
    private String description;
    private String imagePath;
    private String mail;
    private String phone;
    private String web;

    public Sponsor(int id, String name, String description, String imagePath, String mail, String phone, String web) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.mail = mail;
        this.phone = phone;
        this.web = web;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public String getWeb() {
        return web;
    }

    @Override
    public String toString() {
        return "Sponsor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", web='" + web + '\'' +
                '}';
    }

    public boolean equalsId(Sponsor s) {
        return this.id == s.getId();
    }
}
