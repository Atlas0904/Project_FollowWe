package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by atlas on 2016/8/24.
 */
public class UserAddedPointEventListener implements ValueEventListener, ChildEventListener {

    private static final String TAG = UserAddedPointEventListener.class.getSimpleName();
    private static Context context;
    private static Firebase ref;
    private static Query queryRef;
    private UserPlace userPlace;

    public HashMap<String, UserPlaceMisc> userPlaces;

    public UserAddedPointEventListener(Context context, Firebase root, CurrentUserInfo currentUserInfo) {
        this.context = context;
        ref = root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo)).child(NodeDefineOnFirebase.NODE_USER_ADDED_MARKER);
        ref.addValueEventListener(this);
        ref.addChildEventListener(this);
        Log.d(TAG, "ref=" + ref);
    }

    public static void setValue(UserPlace userPlace) {
        Log.d(TAG, "setValue: userPlace" + userPlace);
        // Use lat_lng replace "." to "d" as unique id
        ref.child(userPlace.id).setValue(userPlace);
    }

    public static void removeValue(LatLng latLng) {
        Log.d(TAG, "removeValue: latLng" + latLng);
        ref.child(UserPlace.getId(latLng)).removeValue();
    }

    public static void query(String key, String value) {
        queryRef = ref.orderByChild(key).equalTo(value);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "query -> onChildAdded: dataSnapshot=" + dataSnapshot + " s=" + s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            userPlace = child.getValue(UserPlace.class);
            Log.d(TAG, "onDataChange: userPlace=" + userPlace);

            addUserPlaceInfoList(userPlace);
        }
    }

    private void addUserPlaceInfoList(UserPlace userPlace) {
        if (userPlaces == null) userPlaces = new HashMap<String, UserPlaceMisc>();

        String id = userPlace.getId();
        if (!userPlaces.containsKey(id)) {
            // put into the map
            LatLng latLng  = new LatLng(userPlace.lat, userPlace.lng);
            Marker marker = GoogleMapEventHandler.addMarker(latLng, userPlace.addr, userPlace.markedby + ": " + userPlace.comment, BitmapDescriptorFactory.HUE_RED);
            userPlaces.put(id, new UserPlaceMisc(userPlace, marker, false));
        } else {
            UserPlaceMisc savedUserPlace = userPlaces.get(id);
            if (!savedUserPlace.userPlace.equals(userPlace)) {
                savedUserPlace.userPlace = userPlace;
                userPlaces.put(id, savedUserPlace);  // Just put into array, update will perform as user click info window
            }
        }
    }

    public Marker getMarker(String id) {
        return userPlaces.get(id).marker;
    }

    public HashMap<String, UserPlaceMisc> getUserPlaces() {
        return userPlaces;
    }

    public static String dump(HashMap<String, UserPlaceMisc> userPlaces) {
        String ret="";
        if (userPlaces != null) {
            Iterator it = userPlaces.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                ret += (pair.getKey() + " = " + (UserPlaceMisc) pair.getValue() + "\n");
            }
        }
        return ret;
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {}

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        // remove point from map
        Log.d(TAG, "onChildRemoved: dataSnap=" + dataSnapshot);
        //for (DataSnapshot child: dataSnapshot.getChildren()) {
            userPlace = dataSnapshot.getValue(UserPlace.class);
            Log.d(TAG, "onChildRemoved: userPlace=" + userPlace);
            removeUserPlace(userPlace);
        //}
    }

    private void removeUserPlace(UserPlace userPlace) {
        Log.d(TAG, "removeUserPlace: userPlace=" + userPlace);
        if (userPlaces.get(userPlace.getId()) != null) {
            userPlaces.get(userPlace.getId()).marker.remove();  // from map
            userPlaces.remove(userPlace.getId());  // from array
        } else {
            Log.e(TAG, "removeUserPlace can not found on array");
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
}
