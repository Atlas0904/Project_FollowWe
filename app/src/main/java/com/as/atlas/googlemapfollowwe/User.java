package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by atlas on 2016/7/13.
 */
public class User implements Serializable {
//    public static final double DEFAULT_LAT = 25.055408;
//    public static final double DEFAULT_LNG = 121.554099;

    private static final double RATIO_OF_REASONABLE_WALKING_RATE = 10;


    public String name;
    public double lat;
    public double lng;

    public int iconNo = R.mipmap.ic_launcher;

    ArriveMethod arriveMethod = ArriveMethod.WALKING;

    enum ArriveMethod{
        WALKING, BICYCLE, CAR
    }

    //Introducing the dummy constructor
    public User() {
        /*Blank default constructor essential for Firebase*/
    }

    public User(String name) {
        this.name = name;
    }

    public User(String name, double lat, double lng, int iconNo) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.iconNo = iconNo;
    }

    //Getters and setters
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public int getIconNo() { return iconNo; }


    public static boolean isReasonableSpeed(ArriveMethod method, double m, long sec) {
        if (sec == 0)  return false;

        double ratio = m/ (double) sec;

        if (ArriveMethod.WALKING == method) {
            return ratio < RATIO_OF_REASONABLE_WALKING_RATE;
        }
        return false;
    }

    @Override
    public String toString() {
        return "[User] name=" + name + " lat=" + lat + " lng=" + lng + " iconNo=" + iconNo;
    }
}
