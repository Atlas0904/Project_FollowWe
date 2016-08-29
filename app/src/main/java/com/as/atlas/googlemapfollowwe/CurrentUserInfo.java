package com.as.atlas.googlemapfollowwe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by atlas on 2016/8/13.
 */
public class CurrentUserInfo implements Serializable {
    public final static String EXTRA_NAME = "NAME";
    public final static String EXTRA_PWD = "PWD";
    public final static String EXTRA_NUMBER_ROOM = "ROOM_NUMBER";
    public final static String EXTRA_ICON_NO = "EXTRA_ICON_NO";

    public String name = "User01";
    private String pwd = "PWD";
    public int roomNo = 904;
    public int iconNo = R.mipmap.ic_launcher;

    public final static LatLng LATLNG_101 = new LatLng(25.033408, 121.564099);
    public LatLng latLng = LATLNG_101;

    public Place destination;
    public UserRoute userRoute;
    public Date time = new Date();

    public CurrentUserInfo() {
    }

    public CurrentUserInfo(String name, String pwd, int roomNo) {
        this.name = name;
        this.pwd = pwd;
        this.roomNo = roomNo;
        this.userRoute = new UserRoute(name);
    }

    public CurrentUserInfo(String name, int iconNo) {
        this.name = name;
        this.iconNo = iconNo;
        this.userRoute = new UserRoute(name);
    }

    // Remove due to we dont use transient
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//        out.writeDouble(latLng.latitude);
//        out.writeDouble(latLng.longitude);
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        latLng = new LatLng(in.readDouble(), in.readDouble());
//    }

    @Override
    public String toString() {
        return "CurrentUserInfo: name=" + name + " pwd=" + pwd +
                " roomNo=" + roomNo + " latLng=" + latLng +
                " destination=" + destination + " iconNo=" +iconNo +
                " userRoute= " + userRoute +
                " time= " + time
                ;
    }
}
