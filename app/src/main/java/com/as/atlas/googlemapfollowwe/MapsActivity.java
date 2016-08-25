package com.as.atlas.googlemapfollowwe;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_LONG;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnInfoWindowLongClickListener, RoutingListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final long LOCATION_REQUEST_INTERVAL_MS = 500;
    private static final long LOCATION_FAST_REQUEST_INTERVAL_MS = 250;
    private static final float LEVEL_ZOOM_IN = 15.5f;
    private static final String EXTRA_LATS = "extra_lats";
    private static final String EXTRA_LNGS = "extra_lngs";
    private static final int REQUSET_ACCESS_FINE_LOCATION = 1;
    private static boolean mLockedOnUserView = false;


    public final String URL_FIREBASE = "https://followwe-7f0e8.firebaseio.com/";


    private Button buttonSend;
    private TextView textViewLatitude;
    private TextView textViewLongtitude;
    private TextView textViewClickedLatLng;
    public static TextView textViewAddress;  // May cause leak
    private TextView textViewDestination;
    public TextView textViewDuration;
    public TextView textViewDistance;


    public static final String FLOATING_ACTION_BUTTON_DESTINATION = "Dest";

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
    private ArrayList<Polyline> polylines;

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routes, int shortestPathIndex) {

        // Reset first
        if (polylines != null) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            //In case of more than 5 alternative routes
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.YELLOW);
            polyOptions.width(40);
            polyOptions.addAll(routes.get(i).getPoints());
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }


    @Override
    public void onRoutingCancelled() {

    }


    //Local variable
    public class UIHandler extends Handler {
        public final static int EVENT_UI_UPDATE_DURATION = 1;
        public final static int EVENT_UI_UPDATE_DISTANCE = 2;
        private MapsActivity mapsActivity;

        public UIHandler(MapsActivity mapsActivity) {
            this.mapsActivity = mapsActivity;
        }


        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "UIHandler: msg.what=" + msg.what);
            switch (msg.what) {
                case EVENT_UI_UPDATE_DISTANCE:
                    TextView textView = (TextView) mapsActivity.findViewById(R.id.textViewDistance);
                    textView.setText((int) msg.arg1);
                    break;
                case EVENT_UI_UPDATE_DURATION:
                    TextView textView1 = (TextView) mapsActivity.findViewById(R.id.textViewDuration);
                    textView1.setText((int) msg.arg1);
                    break;
                default:
                    break;
            }
        }
    }

    protected UIHandler mUIHandler;

    public final static int EVENT_RETURN_SEARCH_ADDRESS_RESULT = 1;


    private MapPlaceSelectionListener mapPlaceSelectionListener;
    private UserInfoValueEventListener userInfoValueEventListener;
    private UserOnlineChangeValueEventListener userOnlineChangeValueEventListener;
    private DestinationValueEventListener destinationValueEventListener;
    private UserAddedPointEventListener userAddedPointEventListener;

    private MapInfoWindowAdapter mapInfoWindowAdapter;

    private GoogleMapEventHandler googleMapEventHandler;
    private OnMapReadyCallback onMapReadyCallback;

    public com.as.atlas.googlemapfollowwe.Place globalPlace;


    // Current User Info
    private static CurrentUserInfo currentUserInfo;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    // Note: remember to add package in Google console
    // https://console.developers.google.com/apis/credentials?project=at-shareyourlocation

    List<LatLng> userRoute;
    Polyline line;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_maps_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: id=" + id);

        switch (id) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: back to home");
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.menu_save_points:
                Log.d(TAG, "onOptionsItemSelected: menu_save_points");
                mapPlaceSelectionListener.saveMarkerToSharePref();
                break;
            case R.id.menu_clear_points:
                Log.d(TAG, "onOptionsItemSelected: menu_clear_points");
                mapPlaceSelectionListener.resetAllMarkerOnMap();
                break;
            case R.id.menu_sync_to_cloud:
                break;
            case R.id.menu_show_info:
                showInfo();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showInfo() {
        Log.d(TAG, "showInfo");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Icon contributed & Thanks to");
        alertDialogBuilder.setMessage(R.string.icon_source).setCancelable(false);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (null != savedInstanceState) {
            double[] lats = savedInstanceState.getDoubleArray(EXTRA_LATS);
            double[] lngs = savedInstanceState.getDoubleArray(EXTRA_LNGS);
            if (lats != null && lngs != null) {
                for (int i = 0; i < lats.length; i++) {
                    if (userRoute == null) userRoute = new ArrayList<LatLng>();
                    userRoute.add(new LatLng(lats[i], lngs[i]));
                }
            }
        }

        setContentView(R.layout.activity_maps);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if ("".equals(currentUserInfo.destination)) {
                sendDestionationToServer(currentUserInfo.destination);
                //}
            }
        });

        textViewLatitude = (TextView) findViewById(R.id.textViewLatitude);
        textViewLongtitude = (TextView) findViewById(R.id.textViewLongitude);
        textViewClickedLatLng = (TextView) findViewById(R.id.textViewClickedLatLng);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewDestination = (TextView) findViewById(R.id.textViewDestination);
        textViewDestination.setVisibility(View.GONE);

        textViewDuration = (TextView) findViewById(R.id.textViewDuration);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.darkgreen));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) MapsActivity.this.findViewById(R.id.textViewDestination);
                if ("".equals(textView.getText().toString()) || FLOATING_ACTION_BUTTON_DESTINATION.equals(textView.getText().toString())) {
                    GoogleMapEventHandler.moveCamera(currentUserInfo.latLng, 16);
                    Toast.makeText(MapsActivity.this, "Synced", Toast.LENGTH_SHORT).show();
                } else {
                    fab.setBackgroundTintList(ContextCompat.getColorStateList(MapsActivity.this, R.color.darkgreen));
                    textView.setText(FLOATING_ACTION_BUTTON_DESTINATION);

                    com.as.atlas.googlemapfollowwe.Place destination = destinationValueEventListener.getPlace();
                    LatLng latLng = new LatLng(destination.lat, destination.lng);
                    // add Marker on map
                    GoogleMapEventHandler.addMarker(latLng, destination.address, BitmapDescriptorFactory.HUE_YELLOW);
                    GoogleMapEventHandler.moveCamera(latLng, 16);
                    Log.d(TAG, "fab.setOnClickListener: latLng=" + latLng);

                    navigateToDestination(AbstractRouting.TravelMode.DRIVING, currentUserInfo.latLng, latLng);
                }
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TextView textView = (TextView) MapsActivity.this.findViewById(R.id.textViewDestination);
                fab.setBackgroundTintList(ContextCompat.getColorStateList(MapsActivity.this, R.color.darkgreen));
                textView.setText(FLOATING_ACTION_BUTTON_DESTINATION);
                return false;
            }
        });


        configGoogleApiClient();
        configLocationRequest();

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mapPlaceSelectionListener = new MapPlaceSelectionListener(this);
        autocompleteFragment.setOnPlaceSelectedListener(mapPlaceSelectionListener);


        // Firebase section
        Firebase.setAndroidContext(this);
        mFirebase = new Firebase(URL_FIREBASE);

        createUser();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mUIHandler = new UIHandler(MapsActivity.this);
    }

    private void navigateToDestination(AbstractRouting.TravelMode method, LatLng start, LatLng end) {
        Routing routing = new Routing.Builder()
                //.travelMode(AbstractRouting.TravelMode.WALKING)   // 指定路徑
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .waypoints(start, end)   // 起點終點
                .withListener(this)
                .build();

        routing.execute();
    }

    private void sendDestionationToServer(com.as.atlas.googlemapfollowwe.Place place) {
        Log.d(TAG, "sendDestionationToServer: place=" + place);

        UserPlace userPlace = new UserPlace(place.lat, place.lng);
        userAddedPointEventListener.setValue(userPlace);

        if (place.lat != 0.0 || place.lng != 0.0) {
            destinationValueEventListener.setPlace(place);
        }
    }

    private void createUser() {
        String name = getIntent().getStringExtra(CurrentUserInfo.NAME);
        int iconNo = getIntent().getIntExtra(CurrentUserInfo.ICON_NO, R.mipmap.ic_launcher);

        currentUserInfo = (name != null) ? new CurrentUserInfo(name, iconNo) : null;
        Log.d(TAG, "onCreate: " + currentUserInfo);

        // Put user to user
        if (currentUserInfo != null) updateUserToFirebase(currentUserInfo);
    }

    public static CurrentUserInfo getCurrentUserInfo() {
        return currentUserInfo;
    }

    private void updateUserToFirebase(CurrentUserInfo currentUserInfo) {

        Log.d(TAG, "updateUserToFirebase: currentUserInfo=" + currentUserInfo);

        // Assigned default value first. Wait for Google map ready and update currnet value
        User user = new User(currentUserInfo.name, currentUserInfo.latLng.latitude, currentUserInfo.latLng.longitude, currentUserInfo.iconNo);

        mFirebaseRoomInfo = mFirebase.child(NodeDefineOnFirebase.NODE_ROOM_NO);


        userOnlineChangeValueEventListener = new UserOnlineChangeValueEventListener(mFirebase, currentUserInfo);  // set root
        userOnlineChangeValueEventListener.setUser(user);

        destinationValueEventListener = new DestinationValueEventListener(this, mFirebase, currentUserInfo);
        userAddedPointEventListener = new UserAddedPointEventListener(this, mFirebase, currentUserInfo);


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
        User atlas = new User("Atlas", 25.033408, 121.564099, R.mipmap.icon_boy_0);
        User sandy = new User("Sandy", 25.043408, 121.564099, R.mipmap.icon_boy_1);
        User warhol = new User("Warhol", 25.043408, 121.574099, R.mipmap.icon_boy_2);

        mFirebaseUserInfo.push().setValue(atlas);    // 如果本身就是 class, 就不要再用  child("Person")
        mFirebaseUserInfo.push().setValue(sandy);
        mFirebaseUserInfo.push().setValue(warhol);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {  // Note: init may wait for google map ready
        Log.d(TAG, "onMapReady: map=" + googleMap);
        this.googleMap = googleMap;
        googleMapEventHandler = new GoogleMapEventHandler(this, googleMap);
        mapPlaceSelectionListener.putMarkerListToMap();
        userInfoValueEventListener = new UserInfoValueEventListener(googleMapEventHandler);
        mFirebaseUserInfo.addValueEventListener(userInfoValueEventListener);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermisfsions for more details.
            Log.d(TAG, "Permission check for setMyLocationEnable");
            return;
        }

        // GoogleMap event
        googleMap.setOnMapClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnInfoWindowLongClickListener(this);
        googleMap.setMyLocationEnabled(true);

        mapInfoWindowAdapter = new MapInfoWindowAdapter(this, userAddedPointEventListener);
        googleMap.setInfoWindowAdapter(mapInfoWindowAdapter);

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
                log("mOnlineChangeListener: connected=" + connected);
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
    protected void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (userRoute != null) {
            double[] lats = new double[userRoute.size()];
            double[] lngs = new double[userRoute.size()];
            for (int i = 0; i < userRoute.size(); i++) {
                lats[i] = userRoute.get(i).latitude;
                lngs[i] = userRoute.get(i).longitude;
            }
            outState.putDoubleArray(EXTRA_LATS, lats);
            outState.putDoubleArray(EXTRA_LNGS, lngs);
        }
        mapPlaceSelectionListener.saveMarkerToSharePref();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mapPlaceSelectionListener.putMarkerListToMap();
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
        Log.d(TAG, "onConnected Bundle: " + bundle);
        onMapConnectedToDo();
    }

    private void onMapConnectedToDo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUSET_ACCESS_FINE_LOCATION);
            return;
        }

        googleMap.setMyLocationEnabled(true);

        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MapsActivity.this);
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        Log.d(TAG, "onConnected: currentLocation=" + currentLocation);
        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());   // 有可能 Geany 一開始給錯  導致沒有路線圖
//            GoogleMapEventHandler.addMarker(latLng, latLng.toString(), BitmapDescriptorFactory.HUE_VIOLET);
//            GoogleMapEventHandler.moveCamera(latLng, 16);
            Toast.makeText(this, "Google map connected. Position: " + latLng, Toast.LENGTH_SHORT).show();
            GoogleMapEventHandler.moveCamera(latLng, 16);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUSET_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                onMapConnectedToDo();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Log.d(TAG, "onRequestPermissionsResult: permission denied");
            }
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

        LatLng fromLoc = new LatLng(currentUserInfo.latLng.latitude, currentUserInfo.latLng.longitude);
        LatLng toLoc = new LatLng(location.getLatitude(), location.getLongitude());

        if (userRoute == null)  userRoute = new ArrayList<>();
        userRoute.add(toLoc);

        if (line != null) line.remove();

        PolylineOptions points = new PolylineOptions();
        for (LatLng pt: userRoute) {
            points.add(pt);
        }
        line = googleMap.addPolyline(points.width(30).color(Color.RED));

//        Polyline line = googleMap.addPolyline(new PolylineOptions()
//                .add(fromLoc, toLoc)
//                .width(30)
//                .color(Color.RED));

        userOnlineChangeValueEventListener.updateCurrentUserLocation(currentUserInfo, location);

        if (mLockedOnUserView) {
            GoogleMapEventHandler.moveCamera(currentUserInfo.latLng, 16);
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=" + resultCode);
//        if (requestCode == RequestCode.REQUEST_CODE_LOGIN_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//                String name = data.getStringExtra(CurrentUserInfo.NAME);
//                currentUserInfo = (name != null) ? new CurrentUserInfo(name) : null;
//                Log.d(TAG, "onActivityResult: " + currentUserInfo);
//            }
//
//        } else if (requestCode == RequestCode.REQUEST_CODE_PLACE_AUTOCOMPLETE_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                Log.i(TAG, "Place: " + place.getName());   // fragment return
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                // TODO: Handle the error.
//                Log.i(TAG, status.getStatusMessage());
//
//            } else if (resultCode == RESULT_CANCELED) {
//                // The user canceled the operation.
//            }
//        }
//    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    private String secondsToString(int pTime) {
        return String.format("%02d:%02d", pTime / 60, pTime % 60);
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        Log.d(TAG, "onMapClick latLng:" + latLng);
        float zoom = googleMap.getCameraPosition().zoom;
        Log.d(TAG, "addMarkerToList: latLng=" + latLng + " zoom=" + zoom);
        if (zoom < LEVEL_ZOOM_IN) {
            Toast.makeText(this, "Zoom in before add marker!", Toast.LENGTH_SHORT).show();
            return;
        }
        textViewClickedLatLng.setText(latLng.toString());

        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Runnable r = mapPlaceSelectionListener.createSearchAddressThread(geocoder, latLng);
        new Thread(r).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final com.as.atlas.googlemapfollowwe.Place place = Utils.getDurationOfTravel("driving", currentUserInfo.latLng, latLng);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        textViewDistance.setText(secondsToString(place.duration));
                        textViewDuration.setText(Integer.valueOf(place.distance)+ "m");
                    }
                });

//                Message msgDuration = mUIHandler.obtainMessage(mUIHandler.EVENT_UI_UPDATE_DURATION);
//                msgDuration.arg1 = place.duration;
//                mUIHandler.sendMessage(msgDuration);
//
//                Message msgDist = mUIHandler.obtainMessage(mUIHandler.EVENT_UI_UPDATE_DISTANCE);
//                msgDist.arg1 = place.duration;
//                mUIHandler.sendMessage(msgDist);

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
    public void onInfoWindowClick(final Marker marker) {
        String title = marker.getTitle();
        final String snippetOrig = marker.getSnippet();

        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View view = layoutInflater.inflate(R.layout.dialog_input_chat_message, null);

        TextView textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
        textViewUserName.setText(currentUserInfo.name);

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setView(view);
        builder.setTitle("Follow We: Leave Message");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                EditText editTextMsg = (EditText) view.findViewById(R.id.editTextChatMsg);
                String msg = editTextMsg.getText().toString();
                addMessageToInfoWindow(marker, msg);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        }).show();

    }

    private void addMessageToInfoWindow(Marker marker, String msg) {
        LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        UserPlace userPlace = userAddedPointEventListener.getUserPlaces().get(UserPlace.getId(latLng)).userPlace;
        userPlace.userMessages.add(new UserMessage(currentUserInfo.name, msg, Utils.getCurrentTimeStamp()));
        userAddedPointEventListener.setValue(userPlace);
        marker.hideInfoWindow();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        marker.remove();
    }
}
