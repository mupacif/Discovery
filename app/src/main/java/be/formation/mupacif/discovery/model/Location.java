package be.formation.mupacif.discovery.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by student on 20-03-17.
 */
public class Location {

    private long id;
    private String name;
    private LatLng location;

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
