package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by atlas on 2016/8/18.
 */
public class Place {

    public double lat;
    public double lng;
    public String address;
    public float placeId;
    public int duration;
    public int distance;
    public int score;
    public String comment;
    public String timestamp;
    public String filename;


    //Introducing the dummy constructor
    public Place() {
        /*Blank default constructor essential for Firebase*/
    }

    public Place(double lat, double lng, String address) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    @Override
    public String toString() {
        return "[Place] lat=" + lat + " lng=" + lng  + " address=" + address + " placeId:" + placeId + " duration:" + duration + " distance=" + distance +
                " score=" + score + " comment=" + comment + " timestamp=" + timestamp + " filename=" + filename;
    }
}
