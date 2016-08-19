package com.as.atlas.googlemapfollowwe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;


import java.util.HashMap;

/**
 * Created by atlas on 2016/8/18.
 */
public class DestinationValueEventListener implements ValueEventListener, ChildEventListener {

    private static final String TAG = DestinationValueEventListener.class.getSimpleName();
    private Context context;
    private Firebase root;
    private Firebase ref;


    public DestinationValueEventListener(Context context, Firebase root, CurrentUserInfo currentUserInfo) {
        this.context = context;
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
        Log.d(TAG, "onDataChange: dataSnap=" + dataSnapshot);
        for (DataSnapshot child: dataSnapshot.getChildren()) {
            final Place place = child.getValue(Place.class);
            Log.d(TAG, "onDataChange: place= " + place);

            final LatLng latLng = new LatLng(place.lat, place.lat);

            // accept?
//            LayoutInflater layoutInflater = LayoutInflater.from(context);
//            final View view = layoutInflater.inflate(R.layout.dialog_input_message, null);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setView(view)
//                    .setTitle("Follow We: New destination, set as new Marker?");
//            //.setMessage("Do you want add marker on map?")
//            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//
//                    // add Marker on map
//                    GoogleMapEventHandler.addMarker(latLng, place.address, BitmapDescriptorFactory.HUE_YELLOW);
//                    GoogleMapEventHandler.moveCamera(latLng, 16);
//                }
//            })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();

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
