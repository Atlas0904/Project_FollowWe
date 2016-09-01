package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;

/**
 * Created by atlas on 2016/9/1.
 */
abstract public class BaseValueEventListener<T> implements ValueEventListener, ChildEventListener {

    public final static String NODE_USER_ID = "UserId";
    public final static String NODE_ROOM_NO = "RoomNo";
    public final static String NODE_PRESENCE = "presence";
    public final static String NODE_USER = "user";
    public final static String NODE_DESTINATION = "destination";
    public final static String NODE_USER_ADDED_MARKER = "user_added_marker";

    private static final String TAG = BaseValueEventListener.class.getSimpleName();
    protected Context mContext;
    protected Firebase mRef;
    protected Class mClazz;

    public BaseValueEventListener(Context context, Firebase ref, Class clazz) {
        mContext = context;
        mRef = ref;
        mClazz = clazz;
        mRef.addValueEventListener(this);
        mRef.addChildEventListener(this);
    }

    public void setValue(String index, T t) {
        Log.d(TAG, "setValue: data=" + t);
        mRef.child(index).setValue(t);
    }

    public void removeValue(String index) {
        Log.d(TAG, "removeValue: index=" + index);
        mRef.child(index).removeValue();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onDataChange: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            T t = (T) child.getValue(mClazz);
            onDataChangeAction(t);
        }
    }

    protected abstract void onDataChangeAction(T t);

    @Override
    public void onCancelled(FirebaseError firebaseError) {}

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {}

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
}
