package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by atlas on 2016/8/24.
 */
public class UserAddedPointEventListener implements ValueEventListener, ChildEventListener {

    private static final String TAG = UserAddedPointEventListener.class.getSimpleName();
    private final Context context;
    private final Firebase ref;
    private UserPlace userPlace;

    public UserAddedPointEventListener(Context context, Firebase root, CurrentUserInfo currentUserInfo) {
        this.context = context;
        ref = root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo)).child(NodeDefineOnFirebase.NODE_USER_ADDED_MARKER);
        ref.addValueEventListener(this);
        ref.addChildEventListener(this);
        Log.d(TAG, "ref=" + ref);
    }

    public void setValue(UserPlace userPlace) {
        Log.d(TAG, "setValue: ref" + ref);
//        ref.child(NodeDefineOnFirebase.NODE_USER_ADDED_MARKER).setValue(userPlace);   // !!!!! 記得要設定乘 ref 的 user name 的... 要多一個 index
        ref.child(userPlace.id).setValue(userPlace);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            userPlace = child.getValue(UserPlace.class);
            Log.d(TAG, "onDataChange: userPlace=" + userPlace);
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

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
}
