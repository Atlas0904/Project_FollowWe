package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by atlas on 2016/8/25.
 */
public class UserPlaceMisc {

    private static final String TAG = UserPlaceMisc.class.getSimpleName();
    UserPlace userPlace;
    Marker marker;
    boolean changed = false;

    public UserPlaceMisc(UserPlace userPlace, Marker marker, boolean changed) {
        this.userPlace = userPlace;
        this.marker = marker;
        this.changed = changed;
    }

    @Override
    public String toString() {
        return "["+ TAG + "] userPlace= "+ userPlace + " marker=" + marker + " changed=" + changed;
    }
}
