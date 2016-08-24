package com.as.atlas.googlemapfollowwe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by atlas on 2016/8/24.
 */
public class UserPlace {
    public String id ="";
    public String addr ="";
    public String markedby ="";
    public double lat = 0d;
    public double lng = 0d;
    int start = 0;
    public List<UserMessage> userMessages;


    public UserPlace() {}

    public class UserMessage {
        public String user;
        public String msg;
        public String timestamp;
        public UserMessage() {}
    }


    public UserPlace(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.id = String.valueOf(lat) + "_" + String.valueOf(lng);
        this.id = id.replace(".","d");
        userMessages = new ArrayList<UserMessage>();
    }


    private String adjustId() {
        this.id = String.valueOf(lat) + "_" + String.valueOf(lng);
        return id;
    }

    @Override
    public String toString() {
        return "[UserPlace] " +
                " id=" + id +
                " addr=" + addr +
                " markedby=" + markedby +
                " lat=" +lat +
                " lng=" + lng +
                " start=" + start +
                " userMessage=" + userMessages;
    }
}
