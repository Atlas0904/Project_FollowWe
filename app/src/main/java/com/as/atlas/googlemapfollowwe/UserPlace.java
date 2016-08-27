package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atlas on 2016/8/24.
 */
public class UserPlace {
    private final static String CHAR_ORI = ".";
    private final static String CHAR_REPLACE = "d";

    public String id ="";
    public String addr ="";
    public String markedby ="";
    public String comment = "(comment)";
    public double lat = 0d;
    public double lng = 0d;
    public float color;
    int star = 0;
    public List<UserMessage> userMessages;


    public UserPlace() {}
    public UserPlace(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.id = getId();
        userMessages = new ArrayList<UserMessage>();
    }

    public static String getId(LatLng latLng) {
        String ret = String.valueOf(latLng.latitude) + "_" + String.valueOf(latLng.longitude);
        ret = ret.replace(CHAR_ORI, CHAR_REPLACE);
        return ret;
    }


    public String getId() {
        String ret = String.valueOf(lat) + "_" + String.valueOf(lng);
        ret = ret.replace(CHAR_ORI, CHAR_REPLACE);
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof UserPlace))  return false;
        if (this.userMessages == null || ((UserPlace) o).userMessages == null)  return false;

        UserPlace u = (UserPlace) o;
        return this.id.equals(u.id) &&
                this.addr.equals(u.addr) &&
                this.markedby.equals(u.markedby) &&
                this.comment.equals(u.comment) &&
                this.lat == u.lat &&
                this.lng == u.lng &&
                this.star == u.star &&
                this.userMessages.size() == u.userMessages.size() &&
                this.userMessages.equals(u.userMessages);
    }

    @Override
    public String toString() {
        return "[UserPlace] " +
                " id=" + id +
                " addr=" + addr +
                " markedby=" + markedby +
                " comment=" + comment +
                " lat=" +lat +
                " lng=" + lng +
                " start=" + star +
                " userMessage=" + userMessages;
    }
}
