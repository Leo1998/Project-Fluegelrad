package de.projectfluegelrad.database;

public class Sponsor {

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
}
