package com.as.atlas.googlemapfollowwe;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by atlas on 2016/8/10.
 */
public class MapPlaceSelectionListener extends Handler implements PlaceSelectionListener {
    private static final String TAG = MapPlaceSelectionListener.class.getSimpleName();
    private static final int EVENT_ON_SUGGEST_PLACE_DONE = 0;

    private static final String KEY_LATLNG = "place";

    private String suggestedPlace;

    @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.i(TAG, "Place: " + place.getName());//get place details here
        suggestedPlace = (String) place.getName();
        new Thread(new SearchLatLngThread()).start();

    }

    public class SearchLatLngThread implements Runnable {
        public void run() {
            double[] d = Utils.getLatLngFromGoogleMapAPI(suggestedPlace);
            Log.d(TAG, "SearchLatLngThread d:" + d[0] + "/" + d[1]);
            Message msg = MapPlaceSelectionListener.this.obtainMessage(EVENT_ON_SUGGEST_PLACE_DONE);
            Bundle data = new Bundle();
            data.putDoubleArray(KEY_LATLNG, d);
            msg.setData(data);
            sendMessage(msg);
        }
    }

    @Override
    public void onError(Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what) {
            case EVENT_ON_SUGGEST_PLACE_DONE:

                double[] d = msg.getData().getDoubleArray(KEY_LATLNG);
                Log.d(TAG, "EVENT_ON_SUGGEST_PLACE_DONE d:" + d[0] + "/" + d[1]);
                final LatLng latLng = new LatLng(d[0], d[1]);

                // add Marker on map
                GoogleMapEventHandler.addMarker(latLng, suggestedPlace, BitmapDescriptorFactory.HUE_YELLOW);
                GoogleMapEventHandler.moveCamera(latLng, 16);

            default:
                break;
        }
    }

    public String getSuggestedPlace() {
        return suggestedPlace;
    }
}
