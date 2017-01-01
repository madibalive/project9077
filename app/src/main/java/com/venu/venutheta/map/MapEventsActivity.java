package com.venu.venutheta.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;
import com.venu.venutheta.R;
import com.venu.venutheta.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import me.tatarka.rxloader.RxLoader;
import me.tatarka.rxloader.RxLoaderManager;
import me.tatarka.rxloader.RxLoaderManagerCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MapEventsActivity extends FragmentActivity implements
        OnMapReadyCallback,GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        EasyPermissions.PermissionCallbacks {

    private static final String TAG = "POST_MAP";
    private GoogleMap mMap;
    protected GoogleApiClient mGoogleApiClient;
    private UiSettings mUiSettings;
    protected Location mLastLocation;
    private PopupMenu popupMenu ;
    private LatLng latLng;
    private Geocoder geocoder;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SETTINGS_SCREEN = 125;
    private static final float DEFAULT_MAP_ZOOM_LEVEL = 15F;
    private static final float DEFAULT_MIMIMUM_CAMERA_MOVEMENT = 2F;
    private boolean mLocationPermissionDenied = false;
    private static final String MAP_VIEW_SAVE_STATE = "mapViewSaveState";

    private Marker mCurrentLocation;
    private MarkerOptions selfMarker;
    private ImageButton searchBtn;
    private EditText locationSearch;

    private String currentLocationCountryName;
    private String currentLocationLocality;
    private String currentLocationSubLocality;
    private String currentLocationFirstAddressLine;
    private String currentLocationName;

    // parse utils here
    private RecyclerView mRecyclerview;
    private List<ParseObject> mDatas=new ArrayList<>();
    private MapEventAdapters mAdapter;
    RxLoaderManager loaderManager;
    RxLoader<List<ParseObject>> eventsRxLoader;
    SupportMapFragment mapFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_events);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(MapEventsActivity.this, Locale.getDefault());

        loaderManager = RxLoaderManagerCompat.get(this);
        initAdapter();

    }

    private void initAdapter(){
        locationSearch = (EditText) findViewById(R.id.enter_search);
        searchBtn = (ImageButton) findViewById(R.id.search);
        searchBtn.setOnClickListener(view -> onMapSearch());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Dirty hack to avoid crash in MapView
        // See https://code.google.com/p/gmaps-api-issues/issues/detail?id=6237#c9
        final Bundle mapViewSaveState = new Bundle(outState);
        mapFragment.onSaveInstanceState(mapViewSaveState);
        outState.putBundle(MAP_VIEW_SAVE_STATE, mapViewSaveState);

        super.onSaveInstanceState(outState);
        mapFragment.onSaveInstanceState(outState);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);

        }

        if (myLocation != null) {



            latLng =  new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            getEvents(latLng);
            CameraPosition selfLoc = CameraPosition.builder()
                    .target(latLng)
                    .zoom(13)
                    .bearing(0)
                    .tilt(45)
                    .build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(selfLoc));
            selfMarker = new MarkerOptions()
                    .position(latLng)
                    .title("Here you are!");

            mCurrentLocation= mMap.addMarker(selfMarker);

            CircleOptions selfCircle = new CircleOptions()
                    .center(latLng)
                    .radius(1000)
                    .strokeColor(getResources().getColor(R.color.venu_red));


            mMap.addCircle(selfCircle);



        }else {



        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(MapEventsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //call location request
    }
    @Override
    public void onMapClick(LatLng latLng2) {
        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
        }


        latLng=latLng2;
//        ParseGeoPoint location = new ParseGeoPoint(latLng.latitude,latLng.longitude);
        selfMarker = new MarkerOptions().position(latLng).title("set Location here");
        mCurrentLocation = mMap.addMarker(selfMarker);
        getEvents(latLng);

    }

    public void onMapSearch() {
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;

        geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
            if (addressList.size()>0){
                Log.i(TAG, "onMapSearch: " + addressList.size());
                showPopup(locationSearch,addressList);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void showPopup(View view,List<Address> list) {
        popupMenu = new PopupMenu(MapEventsActivity.this, view);
        for (int i = 0; i < list.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, list.get(i).toString());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {

            int i = item.getItemId();
            Address address = list.get(i);
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
            getEvents(latLng);

            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            return false;

        });

        popupMenu.show();
    }


    private void setupCircle(LatLng latLng){
        CircleOptions selfCircle = new CircleOptions()
                .center(latLng)
                .radius(1000)
                .strokeColor(getResources().getColor(R.color.venu_red));

        mMap.addCircle(selfCircle);
    }


    @AfterPermissionGranted(MY_LOCATION_PERMISSION_REQUEST_CODE)
    private void loadLocation() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (EasyPermissions.hasPermissions(MapEventsActivity.this, perms)) {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            buildGoogleApiClient();

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.requestLocaion),
                    MY_LOCATION_PERMISSION_REQUEST_CODE, perms);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: " +bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SETTINGS_SCREEN) {
            // Do something after user returned from app settings screen, like showing a Toast.

        }
    }

    private class MapEventAdapters
            extends BaseQuickAdapter<ParseObject> {

        public MapEventAdapters(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder holder, final ParseObject request) {

        }
    }

    private Callback<List<ParseObject>> loadCallBack = new Callback<List<ParseObject>>() {
        @Override
        public void onFinish(List<ParseObject> result, Callable callable, Throwable e) {
            if (e == null) {
                Timber.d("got data");
                mAdapter.setNewData(result);
                if (result.size() > 0)
                    mRecyclerview.setVisibility(View.VISIBLE);
                // On success
            } else {
                // On error
                Timber.d("stated error %s",e.getMessage());

            }
        }

    };

    void getEvents(LatLng term) {
        Timber.d("stated");
        if (NetUtils.hasInternetConnection(getApplicationContext())){
            TaskGetEvents taskLoad = new TaskGetEvents(term);
            Tasks.execute(taskLoad,loadCallBack);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    protected void onResume() {
        super.onResume();
    }

}
