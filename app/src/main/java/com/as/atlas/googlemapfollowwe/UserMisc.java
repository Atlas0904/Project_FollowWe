package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by atlas on 2016/8/14.
 */
public class UserMisc {

    User user;
    Marker marker;
    boolean changed = false;

    public UserMisc(User user, Marker marker, boolean changed) {
        this.user = user;
        this.marker = marker;
        this.changed = changed;
    }

    @Override
    public String toString() {
        return "[UserMisc] user= "+ user + " marker=" + marker + " changed=" + changed;
    }
}
