package com.as.atlas.googlemapfollowwe;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * Created by atlas on 2016/8/10.
 */
public class UserInfoValueEventListener implements ValueEventListener {
    public final static String TAG = UserInfoValueEventListener.class.getSimpleName();
    private GoogleMapEventHandler googleMapEventHandler;

    public UserInfoValueEventListener(GoogleMapEventHandler googleMapEventHandler) {
        Log.d(TAG, "UserInfoValueEventListener ctr googleMapEventHandler:" + googleMapEventHandler);
        this.googleMapEventHandler = googleMapEventHandler;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
//        for (DataSnapshot childPerson: dataSnapshot.getChildren()) {
//            String name = (String) childPerson.child("name").getValue();
//            double lat = (double) childPerson.child("lat").getValue();
//            double lng = (double) childPerson.child("lng").getValue();
//
//            Log.d(TAG, "UserInfoValueEventListener: 1st method data name:"+ name + " lat:" + lat + " lng:" +lng);
//            if (googleMapEventHandler!= null) googleMapEventHandler.showOnlineUserOnMap(name, lat, lng);
//        }

        for (DataSnapshot child: dataSnapshot.getChildren()) {
            User user = child.getValue(User.class);
            Log.d(TAG, "UserInfoValueEventListener: User= " + user);
//            if (googleMapEventHandler!= null) googleMapEventHandler.showOnlineUserOnMap(user.getName(), user.getLat(), user.getLng());
        }
//
//        HashMap<String, User> map = null;
//        for (DataSnapshot child : dataSnapshot.getChildren()) {
//            map = (HashMap<String, User>) child.getValue();
//            Log.d(TAG, "UserInfoValueEventListener: list:" + map);
//        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {
        Log.d(TAG,"The read failed: " + firebaseError.getMessage());
    }
}
