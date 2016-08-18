package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by atlas on 2016/8/18.
 */
public class Place {

    public double lat;
    public double lng;
    String address;
    float placeId;


    //Introducing the dummy constructor
    public Place() {
        /*Blank default constructor essential for Firebase*/
    }

    public Place(double lat, double lng, String address) {

        this.lat = lat;
        this.lat = lng;
        this.address = address;
    }

    @Override
    public String toString() {
        return "[Place] lat=" + lat + " lng=" + lng  + " address=" + address + " placeId:" + placeId;
    }
}
