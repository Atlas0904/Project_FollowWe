package com.as.atlas.googlemapfollowwe;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.firebase.client.Firebase;

public class LocationUpdateService extends Service implements LocationListener {
    private static final String TAG = LocationUpdateService.class.getSimpleName();

    private LocationManager locationManager;
    private User user;
    private int roomNo;

    private Firebase root;
    private Firebase ref;

    public LocationUpdateService() {
        Log.d(TAG, "LocationUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        root = new Firebase(MapsActivity.URL_FIREBASE);
        Log.d(TAG, "conCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: intent=" + intent + " flag=" + flags + " startId=" + startId);
        if (intent != null) {
            roomNo = intent.getIntExtra(MapsActivity.EXTRA_ROOM_NO, 0);
            user = (User) intent.getSerializableExtra(MapsActivity.EXTRA_USER);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.d(TAG, "checkSelfPermission denied");
                return 0;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            ref = root.child(NodeDefineOnFirebase.NODE_ROOM_NO).child(String.valueOf(roomNo)).child(NodeDefineOnFirebase.NODE_USER);

            Log.d(TAG, "onStartCommand: roomNo=" + roomNo + " user=" + user + " ref=" + ref);
        }

        return START_REDELIVER_INTENT;
//        return super.onStartCommand(intent, flags, startId);
    }

    private void setUser(User user) {
        Log.d(TAG, "setUser: user=" + user);
        ref.child(user.name).setValue(user);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d(TAG, "onBind: intent=" + intent);
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: location lat/lng=" + location.getLatitude() + "/" + location.getLongitude());
        if (user != null) {
            user.lat = location.getLatitude();
            user.lng = location.getLongitude();
            setUser(user);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}
}
