package be.formation.mupacif.discovery.model;

import java.io.Serializable;
import java.util.Calendar;


public class Interest  implements Serializable{

    private long id;
    private String title;
    private String description;
    private Location location;
    private Calendar date;

    @Override
    public String toString() {
        return "Interest{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", location=" + location +
                ", description='" + description + '\'' +
                '}';
    }

    public Interest() {
    }

    public Interest(String title, String description, Location location, Calendar date) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
    }

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
