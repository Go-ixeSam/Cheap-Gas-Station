package com.hoanpham.uit.cheapgasstation.Screen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiClient;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiInterface;
import com.hoanpham.uit.cheapgasstation.Base.MarkerInfoWindowAdapter;
import com.hoanpham.uit.cheapgasstation.Base.RouteDecode;
import com.hoanpham.uit.cheapgasstation.R;
import com.hoanpham.uit.cheapgasstation.Server.Direction.model.DirectionResults;
import com.hoanpham.uit.cheapgasstation.Server.Direction.model.Route;
import com.hoanpham.uit.cheapgasstation.Server.Direction.model.Step;
import com.hoanpham.uit.cheapgasstation.Server.NearbyPlace.Geometry;
import com.hoanpham.uit.cheapgasstation.Server.NearbyPlace.NearbyResults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class GoogleMapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private static final String DRIVING_MODE = "driving";
    private static final String NEARBY_TYPE = "gas_station";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int RADIUS = 10000;
    private static final double MIN_DISTANCE = 10; //meters
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private int totalDistance;
    private int totalTime;
    private boolean mFirstZoom = true;
    private Location mCurrentLocation, mPreviousLocation;
    private Marker mPreviousMarker = null;
    private Polyline mPolyline = null;
    private ArrayList<Marker> mListMarker = new ArrayList<>();
    private List<LatLng> mListNearby = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    //    check new move marker realtime
    private static final int ANIMATE_SPEED = 3600;
    private static final int ANIMATE_SPEED_TURN = 1500;
    private static final int BEARING_OFFSET = 15;
    private float tilt = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        initMap();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (mPreviousLocation != null && locationResult.getLastLocation() != null) {
                    double distance = mPreviousLocation.distanceTo(locationResult.getLastLocation());
                    if (distance >= MIN_DISTANCE) {
                        mCurrentLocation = locationResult.getLastLocation();
                        mPreviousLocation = mCurrentLocation;
                        moveCarRealTimeLocation(mCurrentLocation);
//                        onResume();
                    }
                    Log.d("TAGGG", "onLoationResult: 1");
                } else if (locationResult.getLastLocation() != null) {
                    mCurrentLocation = locationResult.getLastLocation();
                    mPreviousLocation = mCurrentLocation;
                    moveCarRealTimeLocation(mCurrentLocation);
//                    onResume();
                    Log.d("TAGGG", "onLocationResult: 2");
                }
            }
        };
    }

    private void updateLocation() {
        if (mFusedLocationClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            Log.d("TAGGG", "updateLocation: ffdfwq");
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, getMainLooper());
        }
    }

    private void checkPermission(){

    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mCurrentLocation = task.getResult();
                            mPreviousLocation = mCurrentLocation;
                            addMarkerToGoogleMap(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                            updateLocation();
                            onResume();
                        } else {
                            Toast.makeText(this, "Error" + Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (SecurityException unlikely) {
            Toast.makeText(this, "Error" + unlikely.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this));
        mGoogleMap.setOnInfoWindowClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setOnMarkerClickListener(this);
        getUserLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mGoogleMap != null && mCurrentLocation != null) {
//            searchGasNearbyCurrentPosition(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//        }
    }

    private void searchGasNearbyCurrentPosition(double lat, double lng) {
        GoogleApiInterface api = GoogleApiClient.createService(GoogleApiInterface.class);
        Single<NearbyResults> resultsSingle = api.getNearByPlace(lat + "," + lng,
                String.valueOf(RADIUS),
                NEARBY_TYPE,
                getResources().getString(R.string.google_map_key_1));
        resultsSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SingleObserver<NearbyResults>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull NearbyResults nearbyResults) {
                        if (nearbyResults.getResults().size() > 0) {
                            for (int i = 0; i < nearbyResults.getResults().size(); i++) {
                                Geometry geo = nearbyResults.getResults().get(i).getGeometry();
                                LatLng latLng = new LatLng(geo.getLocation().getLat(), geo.getLocation().getLng());
                                mListNearby.add(latLng);
                            }
                        }
                        if (mListNearby.size() > 0) {
                            showMarkerInMap(mListNearby, new LatLng(lat, lng));
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
    }

    private void removeListMarkerPlace(List<Marker> placeNearby) {
        for (Marker marker : placeNearby) {
            marker.remove();
        }
    }

    private void showMarkerInMap(List<LatLng> position, LatLng currentLocation) {
        removeListMarkerPlace(mListMarker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18.f));
        for (LatLng latLng : position) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.ic_gas_station));
            markerOptions.position(latLng);
            Marker marker = mGoogleMap.addMarker(markerOptions);
            mListMarker.add(marker);
        }
    }

    private void addMarkerToGoogleMap(double lat, double lng) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(new LatLng(lat, lng));
            mMarkerOptions.icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.ic_car));
            String addressName = getNameLocationFromLatLng(lat, lng);
            mMarkerOptions.title(addressName);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18.f));
            mPreviousMarker = mGoogleMap.addMarker(mMarkerOptions);//remove after move
        }
    }

    private String getNameLocationFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = null;
        try {
            List<Address> mAddressList = geocoder.getFromLocation(lat, lng, 1);
            address = mAddressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        getLastLocation();
    }

    private void moveCarRealTimeLocation(Location location) {
        Log.d("TAGGG", "moveCarRealTimeLocation: ");
        if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
            return;
        }

        if (mPreviousMarker != null) {
            mPreviousMarker.remove();
        }
        mCurrentLocation = location;
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Marker mLatestMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).flat(false).icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.ic_car)));
        mPreviousMarker = mLatestMarker;
        if (mFirstZoom) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    latLng, 18f);
            mGoogleMap.animateCamera(cameraUpdate);
            mFirstZoom = false;
        }
        float bearing = location.getBearing();
        LatLng updatedLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        changePositionSmoothly(mLatestMarker, updatedLatLng, bearing);
//        onResume();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void changePositionSmoothly(final Marker marker, final LatLng newLatLng, final float bearing) {
        final LatLng startPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        final LatLng finalPosition = newLatLng;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                marker.setRotation(bearing);
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = (float) elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                        startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                marker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    /*if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }*/

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.10);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker.getPosition());
                    LatLngBounds bounds = builder.build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            float bearing = bearingTwoLatLng(startPosition, finalPosition);
                            CameraPosition cameraPosition = new CameraPosition.Builder().bearing(bearing).zoom(18.f).target(finalPosition).tilt(tilt).build();
                            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            mCurrentLocation.setLatitude(newLatLng.latitude);
                            mCurrentLocation.setLongitude(newLatLng.longitude);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
//                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), ANIMATE_SPEED_TURN, new GoogleMap.CancelableCallback() {
//                        @Override
//                        public void onFinish() {
//                        }
//
//                        @Override
//                        public void onCancel() {
//
//                        }
//                    });

                }

            }
        });
    }

    private void initMap() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentGoogleMapSupport);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStatusLocation();
                } else {

                }
                return;
            }
        }
    }

    private void checkStatusLocation() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildMessageGPS();
        }
    }

    private void buildMessageGPS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("GPS của bạn hiện đang tắt. Bật GPS để tiếp tục");
        alertDialog.setNegativeButton("Quay lại", (dialog, which) -> dialog.cancel());

        alertDialog.setPositiveButton("Đồng ý", (dialog, which) -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void drawDirectionTwoPoint(LatLng fromLatLng, LatLng toLatLng, boolean addMarkerEndPoint) {
        if (mPolyline != null) {
            mPolyline.remove();
        }
        GoogleApiInterface apiInterface = GoogleApiClient.createService(GoogleApiInterface.class);
        apiInterface.getDirectionWithTwoPoint(fromLatLng.latitude + ", " + fromLatLng.longitude,
                toLatLng.latitude + ", " + toLatLng.longitude,
                DRIVING_MODE,
                getResources().getString(R.string.google_map_key_1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<DirectionResults>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onSuccess(@NonNull DirectionResults directionResults) {
                        ArrayList<LatLng> routelist = new ArrayList<LatLng>();
                        if (directionResults.getRoutes().size() > 0) {
                            ArrayList<LatLng> decodelist;
                            Route routeA = directionResults.getRoutes().get(0);
                            if (routeA.getLegs().size() > 0) {
                                List<Step> steps = routeA.getLegs().get(0).getSteps();
                                totalDistance = routeA.getLegs().get(0).getDistance().getValue();//m
                                totalTime = routeA.getLegs().get(0).getDuration().getValue();//s
                                Step step;
                                String polyline;
                                for (int i = 0; i < steps.size(); i++) {
                                    step = steps.get(i);
                                    routelist.add(new LatLng(step.getStartLocation().getLat(), step.getStartLocation().getLng()));
                                    polyline = step.getPolyline().getPoints();
                                    decodelist = RouteDecode.decodePoly(polyline);
                                    routelist.addAll(decodelist);
                                    routelist.add(new LatLng(step.getEndLocation().getLat(), step.getEndLocation().getLng()));
                                }
                            }
                        }
                        if (routelist.size() > 0) {
                            PolylineOptions mRectLine = new PolylineOptions().width(10).color(
                                    Color.RED);

                            for (int i = 0; i < routelist.size(); i++) {
                                mRectLine.add(routelist.get(i));
                            }
                            mPolyline = mGoogleMap.addPolyline(mRectLine);
                            if (addMarkerEndPoint) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(toLatLng.latitude, toLatLng.longitude));
                                markerOptions.draggable(true);
                                mGoogleMap.addMarker(markerOptions);
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(GoogleMapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this));
        marker.showInfoWindow();
        drawDirectionTwoPoint(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), false);
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this));
        marker.showInfoWindow();
    }

    //    add new move car realtime
    public float bearingTwoLatLng(LatLng first, LatLng second) {
        Location start = convertLatLngToLocation(first);
        Location end = convertLatLngToLocation(second);
        return start.bearingTo(end);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }
}
