package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by atlas on 2016/7/13.
 */
public class User {

    public String name;
    public double lat;
    public double lng;

    public static final double DEFAULT_LAT = 25.055408;
    public static final double DEFAULT_LNG = 121.554099;

    ArriveMethod arriveMethod;

    enum ArriveMethod{
        WALKING, BICYCLE, CAR
    }

    //Introducing the dummy constructor
    public User() {
        /*Blank default constructor essential for Firebase*/
    }

    public User(String name) {
        this.name = name;
        this.lat = DEFAULT_LAT;
        this.lng = DEFAULT_LNG;
    }

    public User(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        arriveMethod = ArriveMethod.WALKING;
    }

    //Getters and setters
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    @Override
    public String toString() {
        return "[User] name=" + name + " lat=" + lat + " lng=" + lng;
    }
}
