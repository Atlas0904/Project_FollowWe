package com.as.atlas.googlemapfollowwe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by atlas on 2016/8/10.
 */
public class MapPlaceSelectionListener extends Handler implements PlaceSelectionListener {
    private static final String TAG = MapPlaceSelectionListener.class.getSimpleName();
    private static final int EVENT_ON_SUGGEST_PLACE_DONE = 0;
    private static final int EVENT_RETURN_SEARCH_ADDRESS_RESULT = 1;

    private static final String KEY_ADDRESS = "address";
    private static final String KEY_LATLNG = "latLng";
    private static final String SHARED_PREFS_MARKOPTIONSSET = "mark_options_set";
    private String suggestedPlace;

    private String PREFERENCE_MARKER_OPTIONS = "pref_marker_options";
    private List<UserMarker> userMarkers = new ArrayList<UserMarker>();
    private List<Marker> markers = new ArrayList<Marker>();

    SharedPreferences appSharedPrefs;
    SharedPreferences.Editor prefsEditor;

    TextView textViewAddress;
    TextView title;
    View view;
    AlertDialog.Builder builder;

    private Context context;
    public MapPlaceSelectionListener(Context context) {
        this.context = context;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();
    }

    @Override
    public void onPlaceSelected(Place place) {
        // TODO: Get info about the selected place.
        Log.i(TAG, "Place: " + place);//get place details here
        Log.d(TAG, "Place attribute=" + place.getAttributions());
        suggestedPlace = (String) place.getAddress();
        new Thread(new SearchLatLngThread()).start();

    }


    public class SearchLatLngThread implements Runnable {
        public void run() {
            Log.d(TAG, "SearchLatLngThread suggestedPlace=" + suggestedPlace);
            double[] d = Utils.getLatLngFromGoogleMapAPI(suggestedPlace);
            // Check some place can not interpret to latLng
            if (d == null) return;
            Log.d(TAG, "SearchLatLngThread d:" + d[0] + "/" + d[1]);
            Message msg = MapPlaceSelectionListener.this.obtainMessage(EVENT_ON_SUGGEST_PLACE_DONE);
            Bundle data = new Bundle();
            data.putDoubleArray(KEY_LATLNG, d);
            msg.setData(data);
            sendMessage(msg);
        }
    }

    public Runnable createSearchAddressThread(Geocoder geocoder, LatLng latLng) {
        return new SearchAddressThread(geocoder, latLng);
    }

    public class SearchAddressThread implements Runnable {

        Geocoder geocoder;
        LatLng latLng;
        List<Address> addresses;

        public SearchAddressThread(Geocoder geocoder, LatLng latLng) {
            this.geocoder = geocoder;
            this.latLng = latLng;
        }

        public void run() {
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = "(No mapping address)";
            address = (addresses != null && addresses.size() != 0 && addresses.get(0) != null) ? addresses.get(0).getAddressLine(0) : address;
            Log.d(TAG, "SearchAddressThread: addresses= " + addresses + " address= " + address + " latLng=" + latLng);

            Message msg = MapPlaceSelectionListener.this.obtainMessage(EVENT_RETURN_SEARCH_ADDRESS_RESULT);
            Bundle data = new Bundle();
            data.putString(KEY_ADDRESS, address);
            data.putDoubleArray(KEY_LATLNG, new double[] {latLng.latitude, latLng.longitude});
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
            case EVENT_ON_SUGGEST_PLACE_DONE: {

                double[] d = msg.getData().getDoubleArray(KEY_LATLNG);
                Log.d(TAG, "EVENT_ON_SUGGEST_PLACE_DONE d:" + d[0] + "/" + d[1]);
                final LatLng latLng = new LatLng(d[0], d[1]);

                // add Marker on map
                GoogleMapEventHandler.addMarker(latLng, suggestedPlace, BitmapDescriptorFactory.HUE_YELLOW);
                GoogleMapEventHandler.moveCamera(latLng, 16);
                com.as.atlas.googlemapfollowwe.Place place = new com.as.atlas.googlemapfollowwe.Place(latLng.latitude, latLng.longitude, suggestedPlace);
                MapsActivity.getCurrentUserInfo().destination = place;
                Log.d(TAG, "EVENT_ON_SUGGEST_PLACE_DONE place=" + place);
                break;
            }
            case EVENT_RETURN_SEARCH_ADDRESS_RESULT: {

                final String addr = (String) msg.getData().getString(KEY_ADDRESS);
                double[] d = msg.getData().getDoubleArray(KEY_LATLNG);
                Log.d(TAG, "handleMessage: addr=" + addr + " d=" + d);

                textViewAddress = (TextView) ((Activity) context).findViewById(R.id.textViewAddress);
                textViewAddress.setText(addr);
                LatLng latLng = new LatLng(d[0], d[1]);

                showAddMarkerDialog(latLng, addr);
                break;
            }
            default:
                break;
        }
    }

    private void showAddMarkerDialog(final LatLng latLng, final String addr) {

        title = new TextView(context);
        title.setText("Follow We!");
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setTextSize(20);
        title.setTextColor(Color.BLACK);

        view = LayoutInflater.from(context).inflate(R.layout.dialog_input_message, null);
        builder = new AlertDialog.Builder(context);

        builder.setView(view);
        builder.setCustomTitle(title);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                EditText editTextMsg = (EditText) view.findViewById(R.id.editTextLeaveMsg);
                String title = editTextMsg.getText().toString();
                //addMarkerToList(latLng, title, addr, BitmapDescriptorFactory.HUE_RED);

                // Add to Firebase server
                UserPlace userPlace = new UserPlace(latLng.latitude, latLng.longitude);
                userPlace.addr = addr;
                userPlace.markedby = MapsActivity.getCurrentUserInfo().name;
                userPlace.comment = title;
                userPlace.star = 5;
                userPlace.userMessages = new ArrayList<UserMessage>();
                UserMessage userMessage = new UserMessage(MapsActivity.getCurrentUserInfo().name, "Welcome to Follow We!", Utils.getCurrentTimeStamp());
                Log.d(TAG, "EVENT_RETURN_SEARCH_ADDRESS_RESULT time:" + Utils.getCurrentTimeStamp());

                userPlace.userMessages.add(userMessage);

                UserAddedPointEventListener.setValue(userPlace);
                UserAddedPointEventListener.query("id", userPlace.id);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();

    }


    public void addMarkerToList(LatLng latLng, String title, String addr, float icon) {
        addUserMarkerToList(latLng, title, addr, BitmapDescriptorFactory.HUE_RED);
        addGooleMarkersToList(latLng, title, addr, BitmapDescriptorFactory.HUE_RED);
    }

    private List<UserMarker> addUserMarkerToList(LatLng latLng, String title, String addr, float icon) {
        if (userMarkers == null) userMarkers = new ArrayList<UserMarker>();
        UserMarker userMarker = new UserMarker(latLng, title, addr, BitmapDescriptorFactory.HUE_RED);
        userMarkers.add(userMarker);
        return userMarkers;
    }

    private List<Marker> addGooleMarkersToList(LatLng latLng, String title, String addr, float icon) {
        if (markers == null) markers = new ArrayList<Marker>();
        Marker marker = GoogleMapEventHandler.addMarker(latLng, title, addr, BitmapDescriptorFactory.HUE_RED);
        markers.add(marker);
        return markers;
    }


    public void saveMarkerToSharePref() {

        Gson gson = new Gson();
        String jsonUserMarkers = gson.toJson(userMarkers);
        Log.d(TAG,"jsonUserMarkers = " + jsonUserMarkers);

        prefsEditor.putString(PREFERENCE_MARKER_OPTIONS, jsonUserMarkers);
        prefsEditor.commit();

        getMarkerOptionSetFromPref();
    }

    public List<UserMarker> getMarkerOptionSetFromPref () {

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(PREFERENCE_MARKER_OPTIONS, "");

        Type type = new TypeToken<List<UserMarker>>(){}.getType();
        userMarkers = gson.fromJson(json, type);
        Log.d(TAG, "getMarkerOptionSetFromPref userMarkers=" + showUserMarkers(userMarkers));

        return userMarkers;
    }

    public void putMarkerListToMap() {
        List<UserMarker> userMarkers = getMarkerOptionSetFromPref();
        if (userMarkers != null) {
            for (Iterator<UserMarker> iter = userMarkers.listIterator(); iter.hasNext(); ) {
                UserMarker userMarker = iter.next();
                Log.d(TAG, "putMarkerListToMap: userMarker=" + userMarker);
                GoogleMapEventHandler.addMarker(userMarker.place, userMarker.title, userMarker.snippet, userMarker.iconIndex);
            }
        }
    }

    public void resetAllMarkerOnMap() {
        clearPrefAndList();
        clearGoogleMarkerAndList();
    }

    private void clearPrefAndList() {
        userMarkers = null;
        Gson gson = new Gson();
        String jsonUserMarkers = gson.toJson(userMarkers);
        Log.d(TAG,"jsonUserMarkers = " + jsonUserMarkers);

        prefsEditor.putString(PREFERENCE_MARKER_OPTIONS, jsonUserMarkers);
        prefsEditor.commit();
    }

    private void clearGoogleMarkerAndList() {
        if (markers != null) {
            for (Iterator<Marker> iter = markers.listIterator(); iter.hasNext(); ) {
                Marker marker = iter.next();
                Log.d(TAG, "clearGoogleMarkerAndList: marker=" + marker);
                marker.remove();
            }
            markers = null;
        }

    }

    private String showUserMarkers(List<UserMarker> userMarkers) {
        String ret = "";
        if (userMarkers != null) {
            for (Iterator<UserMarker> iter = userMarkers.listIterator(); iter.hasNext(); ) {
                UserMarker userMarker = iter.next();
                ret += userMarker;
                Log.d(TAG, "showUserMarkers: user=" + userMarker);
            }
        }
        return ret;
    }

    public String getSuggestedPlace() {
        return suggestedPlace;
    }
}
