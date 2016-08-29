package com.as.atlas.googlemapfollowwe;


/**
 * Created by atlas on 2016/8/17.
 */
public class UserMarker {
    LatLng place;
    String title;
    String snippet;
    float iconIndex;

    public UserMarker(LatLng place, String title, String snippet, float iconIndex) {
        this.place = place;
        this.title = title;
        this.snippet = snippet;
        this.iconIndex = iconIndex;
    }

    @Override
    public String toString() {
        return "[UserMarker] place=" + place
                + " title=" + title
                + " snippet=" + snippet
                + " iconIndex=" + iconIndex;
    }
}
