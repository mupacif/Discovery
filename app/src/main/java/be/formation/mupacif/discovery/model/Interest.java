package be.formation.mupacif.discovery.model;

import java.util.Calendar;


public class Interest {

    private long id;
    private String title;
    private String description;
    private Location location;
    private Calendar date;

    public Interest(long id, String title, String description, Location location, Calendar date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }
}
