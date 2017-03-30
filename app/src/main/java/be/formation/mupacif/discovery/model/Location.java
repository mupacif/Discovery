package be.formation.mupacif.discovery.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by student on 20-03-17.
 */
public class Location implements Serializable {

    private long id;
    private String name;
    private transient LatLng location;

    @Override
    public String toString() {
        return "Location{" +
                "name='" + name + '\'' +
                ", location=" + location +
                '}';
    }

    public Location(long id, String name, LatLng location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Location(String name, LatLng location) {
        this.name = name;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
