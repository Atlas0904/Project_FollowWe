package com.as.atlas.googlemapfollowwe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener, GoogleMap.OnInfoWindowLongClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final long LOCATION_REQUEST_INTERVAL_MS = 500;
    private static final long LOCATION_FAST_REQUEST_INTERVAL_MS = 250;
    private static boolean mLockedOnUserView = false;
    


    public final String URL_FIREBASE = "https://followwe-7f0e8.firebaseio.com/";


    private Button buttonShow;
    private TextView textViewLatitude;
    private TextView textViewLongtitude;
    private TextView textViewClickedLatLng;
    private TextView textViewAddress;

    private CheckBox checkBox;
    private GoogleApiClient googleApiClient;

    // Location請求物件
    private LocationRequest locationRequest;
    private GoogleMap googleMap;
    private Location currentLocation;


    // Firebase section
    private Firebase mFirebase;

    // Firebase chat room section
    private Firebase mFirebaseOnline;
    // Use to indicate current user online on not, show name on firebase
    private Firebase mFirebaseUser;

    // Below need to monitor Firebase change
    private Firebase mFirebaseRoomInfo;
    private Firebase mFirebaseUserInfo;

    private ValueEventListener mOnlineChangeListener;
    private ValueEventListener mUserChangeListener;

    //Local variable
    private Handler mHandler;
    public final static int EVENT_RETURN_SEARCH_ADDRESS_RESULT = 1;


    private MapPlaceSelectionListener mapPlaceSelectionListener;
    private UserInfoValueEventListener userInfoValueEventListener;
    private UserOnlineChangeValueEventListener userOnlineChangeValueEventListener;

    private GoogleMapEventHandler googleMapEventHandler;
    private OnMapReadyCallback onMapReadyCallback;


    // Current User Info
    private CurrentUserInfo currentUserInfo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    // Note: remember to add package in Google console
    // https://console.developers.google.com/apis/credentials?project=at-shareyourlocation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        checkBox = (CheckBox) findViewById(R.id.checkBoxSyncPlace);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLockedOnUserView = ((CheckBox) v).isChecked();
            }
        });

        buttonShow = (Button) findViewById(R.id.buttonShow);
        textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        textViewLongtitude = (TextView) findViewById(R.id.textViewLongitude);
        textViewClickedLatLng = (TextView) findViewById(R.id.textViewClickedLatLng);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what){
                    case EVENT_RETURN_SEARCH_ADDRESS_RESULT:


                        final String addr = (String) msg.getData().getString("address");
                        Log.d(TAG, "handleMessage: addr=" + addr);
                        textViewAddress.setText(addr);

                        double[] d = msg.getData().getDoubleArray("latLng");
                        final LatLng latLng = new LatLng(d[0], d[1]);

                        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
                        final View view = layoutInflater.inflate(R.layout.dialog_input_message, null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setView(view)
                                .setTitle("Follow We: Marker");
                        //.setMessage("Do you want add marker on map?")

                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                EditText editTextMsg = (EditText) view.findViewById(R.id.editTextLeaveMsg);
                                GoogleMapEventHandler.addMarker(latLng, editTextMsg.getText().toString(), addr, BitmapDescriptorFactory.HUE_RED);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                        break;
                }
            }
        };

        configGoogleApiClient();
        configLocationRequest();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mapPlaceSelectionListener = new MapPlaceSelectionListener();
        autocompleteFragment.setOnPlaceSelectedListener(mapPlaceSelectionListener);


        // Firebase section
        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(URL_FIREBASE);

        createUser();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void createUser() {
        String name = getIntent().getStringExtra(CurrentUserInfo.NAME);
        currentUserInfo = (name != null) ? new CurrentUserInfo(name) : null;
        Log.d(TAG, "onCreate: " + currentUserInfo);

        // Put user to user
        if (currentUserInfo != null) updateUserToFirebase(currentUserInfo);
    }

    private void updateUserToFirebase(CurrentUserInfo currentUserInfo) {

        Log.d(TAG, "updateUserToFirebase: currentUserInfo=" + currentUserInfo);

        // Assigned default value first. Wait for Google map ready and update currnet value
        User user = new User(currentUserInfo.name, currentUserInfo.latLng.latitude, currentUserInfo.latLng.longitude);

        mFirebaseRoomInfo = mFirebase.child(NodeDefineOnFirebase.NODE_ROOM_NO);
        mFirebaseRoomInfo.child(String.valueOf(currentUserInfo.roomNo)).child(currentUserInfo.name).setValue(user);
        userOnlineChangeValueEventListener = new UserOnlineChangeValueEventListener(mFirebase, currentUserInfo);  // set root


        mFirebaseUser = mFirebase.child(NodeDefineOnFirebase.NODE_PRESENCE).child(currentUserInfo.name);  // Used to info on-line
        mFirebaseOnline = mFirebase.child(".info/connected");   // User connection with FB server

        mFirebaseUserInfo = mFirebase.child(NodeDefineOnFirebase.NODE_USER_ID);
//        mFirebaseUserInfo.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG, "onChildAdded()");
//                Log.d(TAG, "dataSnapshot:" + dataSnapshot.getValue());
//                Log.d(TAG, "dataSnapshot.child(name):" + dataSnapshot.child("name").getValue());
//                Log.d(TAG, "dataSnapshot.child(lat):" + dataSnapshot.child("lat").getValue());
//                Log.d(TAG, "dataSnapshot.child(lng):" + dataSnapshot.child("lng").getValue());
//                Log.d(TAG, "dataSnapshot.getChildrenCount():" + dataSnapshot.getChildrenCount());
//
//                int i = 0;
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    if (child.getValue() != null)
//                        Log.d(TAG, "child index:" + (i++) + " " + String.valueOf(child.getValue()));
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//            }
//        });

        mFirebaseUserInfo.push().setValue(user);
        Log.d(TAG, "updateUserToFirebase: user=" + user);
        Log.d(TAG, "mFirebaseOnline: " + mFirebaseOnline + " mFirebaseUser:" + mFirebaseUser + " mFirebaseRoomInfo:" + mFirebaseRoomInfo);
        //setPeople();
    }

    private void setPeople() {
        User atlas = new User("Atlas", 25.033408, 121.564099);
        User sandy = new User("Sandy", 25.043408, 121.564099);
        User warhol = new User("Warhol", 25.043408, 121.574099);

        mFirebaseUserInfo.push().setValue(atlas);    // 如果本身就是 class, 就不要再用  child("Person")
        mFirebaseUserInfo.push().setValue(sandy);
        mFirebaseUserInfo.push().setValue(warhol);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {  // Note: init may wait for google map ready
        Log.d(TAG, "onMapReady: map=" + googleMap);
        this.googleMap = googleMap;
        googleMapEventHandler = new GoogleMapEventHandler(googleMap);
        userInfoValueEventListener = new UserInfoValueEventListener(googleMapEventHandler);
        mFirebaseUserInfo.addValueEventListener(userInfoValueEventListener);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnInfoWindowLongClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permission check for setMyLocationEnable");
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // Firebase chatroom section
        // Finally, a little indication of connection status
        mOnlineChangeListener = mFirebaseOnline.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                log("mOnlineChangeListener: connected" + connected);
                if (connected) {
                    mFirebaseUser.onDisconnect().removeValue();
                    mFirebaseUser.setValue(true);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.as.atlas.googlemapfollowwe/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.as.atlas.googlemapfollowwe/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        //disableLocationUpdate();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    private void configGoogleApiClient() {
        Log.d(TAG, "configGoogleApiClient");

        googleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                // For place Id
                        addApi(Places.GEO_DATA_API).
                        addApi(Places.PLACE_DETECTION_API).
                        enableAutoManage(this, this).
                        build();
        googleApiClient.connect();
    }

    private void configLocationRequest() {
        Log.d(TAG, "configLocationRequest");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL_MS);
        locationRequest.setFastestInterval(LOCATION_FAST_REQUEST_INTERVAL_MS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        log("onConnected Bundle: " + bundle);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

            return;
        }

//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                Toast.makeText(MapsActivity.this, "On marker click", LENGTH_LONG).show();
//
//                // Save current place
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
//
//                alertDialog.setTitle(R.string.title_current_location)
//                        .setMessage(R.string.message_current_location)
//                        .setCancelable(true);
//
//                alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent result = new Intent();
//                        result.putExtra("lat", currentLocation.getLatitude());
//                        result.putExtra("lng", currentLocation.getLongitude());
//                        setResult(Activity.RESULT_OK, result);
//                        finish();
//                    }
//                });
//                alertDialog.setNegativeButton(android.R.string.cancel, null);
//                alertDialog.show();
//
//                return true;
//            }
//        });

        googleMap.setMyLocationEnabled(true);
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, MapsActivity.this);


        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());   // 有可能 Geany 一開始給錯  導致沒有路線圖
            GoogleMapEventHandler.addMarker(latLng, latLng.toString(), BitmapDescriptorFactory.HUE_VIOLET);
            GoogleMapEventHandler.moveCamera(latLng, 16);
        }

    }

    private void createLocationRequest() {    // 一直 update
        if (locationRequest == null) {
            locationRequest = new LocationRequest();
            locationRequest.setInterval(LOCATION_REQUEST_INTERVAL_MS);
            //locationRequest.setFastestInterval();  其他 app 拿
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        log("onConnectionFailed connectionResult: " + connectionResult);

        int errorCode = connectionResult.getErrorCode();
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, R.string.google_play_service_missing,
                    LENGTH_LONG).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        log("onLocationChanged location: " + location);
        textViewLatitude.setText(String.valueOf(location.getLatitude()));
        textViewLongtitude.setText(String.valueOf(location.getLongitude()));

        updateCurrentUserLocation(location);

        if (mLockedOnUserView) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentUserInfo.latLng));
        }
    }

    private void updateCurrentUserLocation(Location location) {
        currentUserInfo.latLng = new LatLng(location.getLatitude(), location.getLongitude());
        User user = new User(currentUserInfo.name, currentUserInfo.latLng.latitude, currentUserInfo.latLng.longitude);
        mFirebaseRoomInfo.child(String.valueOf(currentUserInfo.roomNo)).child(currentUserInfo.name).setValue(user);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=" + resultCode);
        if (requestCode == RequestCode.REQUEST_CODE_LOGIN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra(CurrentUserInfo.NAME);
                currentUserInfo = (name != null) ? new CurrentUserInfo(name) : null;
                Log.d(TAG, "onActivityResult: " + currentUserInfo);
            }

        } else if (requestCode == RequestCode.REQUEST_CODE_PLACE_AUTOCOMPLETE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());   // fragment return
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void log(String s) {
        Log.d(TAG, s);
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
            address = addresses!= null ? addresses.get(0).getAddressLine(0) : address;
            Log.d(TAG, "SearchAddressThread: addresses= " + addresses + " address= " + address);

            Message msg = mHandler.obtainMessage(EVENT_RETURN_SEARCH_ADDRESS_RESULT);
            Bundle data = new Bundle();
            data.putString("address", address);
            data.putDoubleArray("latLng", new double[] {latLng.latitude, latLng.longitude});
            msg.setData(data);

            mHandler.sendMessage(msg);

        }
    }


    @Override
    public void onMapClick(final LatLng latLng) {
        Log.d(TAG, "onMapClick latLng:" + latLng);
        textViewClickedLatLng.setText(latLng.toString());

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Runnable r = new SearchAddressThread(geocoder, latLng);
        new Thread(r).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String placeId = Utils.getPlaceIdFromGoogleMapAPI(latLng, 500, "restaurant", "cruise");
                if ("".equals(placeId)) {
                    return;
                }
                Places.GeoDataApi.getPlaceById(googleApiClient, String.valueOf(placeId))
                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
                            @Override
                            public void onResult(PlaceBuffer places) {
                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                    final Place myPlace = places.get(0);
                                    Log.i(TAG, "Place found: " + myPlace.getName());
                                } else {
                                    Log.e(TAG, "Place not found");
                                }
                                places.release();
                            }
                        });
            }
        }).start();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        marker.remove();
    }
}
