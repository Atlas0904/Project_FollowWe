package com.as.atlas.googlemapfollowwe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by atlas on 2016/8/27.
 */
public class UserRoute implements Serializable {
    private static final String TAG = UserRoute.class.getSimpleName();


    public String name;
    public List<LatLng> routes;
    public List<Date> times;
    public int accMile;
    public LatLng lastLatLng =  CurrentUserInfo.LATLNG_101;

    public UserRoute(String name) {
        this.name = name;
        this.routes = new ArrayList<LatLng>();
        this.times = new ArrayList<Date>();
    }

    public void addRoute(double lat, double lng) {
        addRoute(new LatLng(lat, lng));
    }

    public void addRoute(LatLng latLng) {
        if (routes == null) {  // 1st
            routes = new ArrayList<LatLng>();
            times = new ArrayList<Date>();
        } else {
            if (!latLng.equals(lastLatLng) && !CurrentUserInfo.LATLNG_101.equals(lastLatLng)) {
                accMile += Utils.getDistance(lastLatLng, latLng);
            }
        }
        routes.add(latLng);
        times.add(new Date());

        lastLatLng = latLng;
    }

    public List<LatLng> getRoute() {
        return routes;
    }

    public double getAccMile() {
        return accMile;
    }

//    private void writeObject(ObjectOutputStream out) throws IOException {
//        out.defaultWriteObject();
//        out.writeDouble(lastLatLng.latitude);
//        out.writeDouble(lastLatLng.longitude);
//
//        out.writeInt(routes.size());
//        for (int i = 0; i < routes.size(); i++) {
//            out.writeDouble(routes.get(i).latitude);
//            out.writeDouble(routes.get(i).longitude);
//        }
//    }
//
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        lastLatLng = new LatLng(in.readDouble(), in.readDouble());
//
//        int size = in.readInt();
//        for (int i = 0; i < size; i++) {
//            if (routes == null) routes = new ArrayList<LatLng>();
//            routes.add(new LatLng(in.readDouble(), in.readDouble()));
//        }
//    }

    @Override
    public String toString() {
        return "[" + TAG + "]" +
                " name=" + name +
                " routes=" + printRoute() +
                " accMile=" + accMile;
    }

    private String printRoute() {
        String s = "(start)";
        for (LatLng latLng: routes) {
            s += latLng + "->";
        }
        s += "(end)";
        return s;
    }
}
