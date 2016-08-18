package com.as.atlas.googlemapfollowwe;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.util.HashMap;

/**
 * Created by atlas on 2016/8/18.
 */
public class DestinationValueEventListener implements ValueEventListener, ChildEventListener {

    private static final String TAG = DestinationValueEventListener.class.getSimpleName();
    private Firebase root;
    private Firebase ref;


    public DestinationValueEventListener(Firebase root, CurrentUserInfo currentUserInfo) {
        this.root = root;
        ref = this.root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo)).child(NodeDefineOnFirebase.NODE_DESTINATION);
        Log.d(TAG, "DestinationValueEventListener: ref" + ref);
        ref.addValueEventListener(this);
        ref.addChildEventListener(this);
    }

    public void setPlace(Place destination) {
        Log.d(TAG, "DestinationValueEventListener: ref" + ref);
        ref.child(destination.address).setValue(destination);   // !!!!! 記得要設定乘 ref 的 user name 的... 要多一個 index
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "UserOnlineChangeValueEventListener: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            Place place = child.getValue(Place.class);
            Log.d(TAG, "UserOnlineChangeValueEventListener: place= " + place);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
