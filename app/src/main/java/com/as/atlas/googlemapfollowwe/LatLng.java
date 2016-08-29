package com.as.atlas.googlemapfollowwe;

import java.io.Serializable;

/**
 * Created by atlas on 2016/8/29.
 */
public class LatLng implements Serializable {
    private static final String TAG = LatLng.class.getSimpleName();

    public final double latitude;
    public final double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng(com.google.android.gms.maps.model.LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public com.google.android.gms.maps.model.LatLng toGmsLatLng() {
        return new com.google.android.gms.maps.model.LatLng(this.latitude, this.longitude);
    }


    public boolean equals(Object o) {
        if (o == null || !(o instanceof LatLng))  return false;
        else {
            LatLng l = (LatLng) o;
            return  this.latitude == l.latitude && this.longitude == l.longitude;
        }
    }

    @Override
    public String toString() {
        return "[" + TAG + "]" +
                " latitude=" + latitude +
                " longtidue=" + longitude;
    }
}
