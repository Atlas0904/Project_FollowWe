package com.as.atlas.googlemapfollowwe;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by atlas on 2016/8/14.
 */
public class UserOnlineChangeValueEventListener implements ValueEventListener, ChildEventListener{

    private static final String TAG = UserOnlineChangeValueEventListener.class.getSimpleName();
    private Firebase root;
    private BitmapDescriptor defaultIcon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_boy);

    HashMap<String, UserMisc> users;

    public UserOnlineChangeValueEventListener(Firebase root, CurrentUserInfo currentUserInfo) {
        this.root = root;
        root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo)).addValueEventListener(this);
        root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo)).addChildEventListener(this);
        users = new HashMap<String, UserMisc>();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //Log.d(TAG, "UserOnlineChangeValueEventListener: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            User user = child.getValue(User.class);
            Log.d(TAG, "UserOnlineChangeValueEventListener: User= " + user);

            if (!users.containsKey(user.name)) {
                Log.d(TAG, "UserOnlineChangeValueEventListener !contain user name:" + user.name);

                UserMisc userMisc = createUserMisc(user);
                users.put(user.name, userMisc);
            } else {
                Log.d(TAG, "UserOnlineChangeValueEventListener contain user name:" + user.name);
                UserMisc userMisc = users.get(user.name);
                userMisc.changed =  (user.lat != userMisc.user.lat || user.lng != userMisc.user.lng);
                if (userMisc.changed) {
                    userMisc.user.lat = user.lat;
                    userMisc.user.lng = user.lng;
                }
            }
        }
        updateUsersIcon(users);

    }

    private UserMisc createUserMisc(User user) {

        LatLng latLng = new LatLng(user.getLat(), user.getLng());
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.icon_user_girl);
        Marker marker = GoogleMapEventHandler.addMarker(latLng, user.name, latLng.toString(), icon);

        UserMisc userMisc = new UserMisc(user, marker ,false);   //先畫了不要重劃
        return userMisc;
    }

    private void updateUsersIcon(HashMap<String, UserMisc> users) {
        Log.d(TAG, "updateUsersIcon: user=" + printMap(users));

        Iterator it = users.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            UserMisc userMisc = (UserMisc) pair.getValue();
            Log.d(TAG, "updateUsersIcon: userMisc=" + userMisc);

            if (userMisc.changed) {
                // Remove old icon
                userMisc.marker.remove();

                // Add new icon
                User user= userMisc.user;
                LatLng latLng = new LatLng(user.getLat(), user.getLng());
                Marker marker = GoogleMapEventHandler.addMarker(latLng, user.name, latLng.toString(), defaultIcon);
                userMisc.marker = marker;
            }
        }

    }


    public static String printMap(HashMap<String, UserMisc> mp) {
        String ret="";
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            ret += (pair.getKey() + " = " + (UserMisc) pair.getValue() + "\n");
        }
        return ret;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}