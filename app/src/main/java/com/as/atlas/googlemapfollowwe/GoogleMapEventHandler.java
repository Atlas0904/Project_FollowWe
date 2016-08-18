package com.as.atlas.googlemapfollowwe;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by atlas on 2016/8/10.
 */
public class GoogleMapEventHandler extends Handler implements FetchUserBitmapTask.FetchUserBitmapResponse{
    public final static String TAG =GoogleMapEventHandler.class.getSimpleName();
    private static GoogleMap googleMap;

    private static final int LETTER_ON_ICON = 1;
    private static final int ICON_SIZE = 128;

    public GoogleMapEventHandler(GoogleMap googleMap) {
        Log.d(TAG, "GoogleMapEventHandler ctr: googleMap=" + googleMap);
        this.googleMap = googleMap;
    }

    public static GoogleMap getGoogleMapInstance() {
        return googleMap;
    }

    public void showOnlineUserOnMap(String name, double lat, double lng) {
        /*
         * Ref: http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=A|FF0000|000000  // Will show A on above example
         */
        Log.d(TAG, "showOnlineUserOnMap: name=" + name + " lat= " + lat + " lng=" + lng);
        LatLng latLng = new LatLng(lat, lng);
        String url = "http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=" + name.substring(0, LETTER_ON_ICON) + "|FF0000|000000";
        (new FetchUserBitmapTask(latLng, name, latLng.toString(), this)).execute(url);
    }

    public static void moveCamera(LatLng latLng, int scale) {

        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(latLng)
                        .zoom(scale)
                        .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, scale));
    }

    @Override
    public void responseWithFetchUserBitmapResult(LatLng latLng, String title, String snippet, Bitmap bitmap) {
        // Use Google map alpha icon
        /*
        Bitmap scaledBitmap = Utils.scaleBitmap(bitmap, ICON_SIZE, ICON_SIZE);
        addMarker(latLng, title, snippet, scaledBitmap);
        */
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_sandy_32);
        addMarker(latLng, title, snippet, icon);
        moveCamera(latLng, 16);
    }

    public static Marker addMarker(LatLng place, String title, String snippet, Bitmap bitmap) {
        if (bitmap == null)  return null;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title.toString())
                .snippet(snippet.toString())
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        return addMarker(markerOptions);
    }

    public static Marker addMarker(LatLng place, String title, String snippet, BitmapDescriptor icon) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title.toString())
                .snippet(snippet.toString())
                .flat(true)
                .icon(icon);

        return addMarker(markerOptions);
    }

    public static MarkerOptions createMarkerOptions(LatLng latLng, String title, String snippet, float color) {
        return new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color));
    }

    public static Marker addMarker(LatLng latLng, String title, String snippet, float color) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color));
        return addMarker(markerOptions);
    }

    public static Marker addMarker(LatLng latLng, String title, float color) {
        return addMarker(latLng, title, latLng.toString(), color);
    }

    private static Marker addMarker(MarkerOptions markerOptions) {
        if (googleMap == null)  return null;
        return googleMap.addMarker(markerOptions);
    }

}
