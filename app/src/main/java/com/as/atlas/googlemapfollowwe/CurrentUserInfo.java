package com.as.atlas.googlemapfollowwe;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by atlas on 2016/8/13.
 */
public class CurrentUserInfo {
    public final static String NAME = "NAME";
    public final static String PWD = "PWD";
    public final static String NUMBER_ROOM = "ROOM_NUMBER";
    public final static String ICON_NO = "ICON_NO";


    private static final int DEFAULT_ROOM_NO = 904;
    private static final String DEFAULT_PWD = "1234";
    public static final LatLng LATLNG_TAIPEI_101 = new LatLng(25.033408, 121.564099);  // 101

    public String name = "User01";
    private String pwd = DEFAULT_PWD;
    public int roomNo = DEFAULT_ROOM_NO;
    public int iconNo = R.mipmap.ic_launcher;
    public LatLng latLng = LATLNG_TAIPEI_101;
    public Place destination;


    public CurrentUserInfo() {
    }

    public CurrentUserInfo(String name, String pwd, int roomNo) {
        this.name = name;
        this.pwd = pwd;
        this.roomNo = roomNo;
    }

    public CurrentUserInfo(String name, int iconNo) {
        this.name = name;
        this.iconNo = iconNo;
    }

    @Override
    public String toString() {
        return "CurrentUserInfo: name=" + name + " pwd=" + pwd + " roomNo=" + roomNo + " latLng=" + latLng + " destination=" + destination + " iconNo=" +iconNo;

    }
}
