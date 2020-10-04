package com.hoanpham.uit.cheapgasstation.Screen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiClient;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiInterface;
import com.hoanpham.uit.cheapgasstation.Base.MarkerInfoWindowAdapter;
import com.hoanpham.uit.cheapgasstation.R;
import com.hoanpham.uit.cheapgasstation.Server.NearbyPlace.Geometry;
import com.hoanpham.uit.cheapgasstation.Server.NearbyPlace.NearbyResults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {


    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private Marker mYourMarker;
    private Marker mOtherMarker;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int RADIUS = 5000;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<LatLng> mListNearby = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_activity);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentGoogleMapSupport);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(GoogleMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GoogleMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null || location.getLatitude() == 0 || location.getLongitude() == 0) {
                    Toast.makeText(GoogleMapActivity.this, "Có lỗi trong quá trình xác thực vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                addMarkerToGoogleMap(location.getLatitude(), location.getLongitude());
                searchGasNearbyCurrentPosition(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void searchGasNearbyCurrentPosition(double lat, double lng) {
        Log.d("TAGGG", "searchGasNearbyCurrentPosition: ");
        GoogleApiInterface api = GoogleApiClient.createService(GoogleApiInterface.class);
        Single<NearbyResults> resultsSingle = api.getNearByPlace(lat + "," + lng,
                String.valueOf(RADIUS),
                "gas_station",
                getResources().getString(R.string.google_map_key_1));
        resultsSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new SingleObserver<NearbyResults>() {

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull NearbyResults nearbyResults) {

                        Log.d("TAGGG", "onSuccess: " + nearbyResults.getResults());
                        if (nearbyResults.getResults().size() > 0) {
                            for (int i = 0; i < nearbyResults.getResults().size(); i++) {
                                Geometry geo = nearbyResults.getResults().get(i).getGeometry();
                                LatLng latLng = new LatLng(geo.getLocation().getLat(), geo.getLocation().getLng());
                                mListNearby.add(latLng);
                            }
                        }
                        if (mListNearby.size() > 0) {
//                            mHandler.post(() -> {
                            showMarkerInMap(mListNearby, new LatLng(lat, lng));
//                            });
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                });
    }

    private void showMarkerInMap(List<LatLng> position, LatLng currentLocation) {
        mGoogleMap.clear();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.f));
        addMarkerToGoogleMap(currentLocation.latitude, currentLocation.longitude);
        for (LatLng latLng : position) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.ic_location_icon));
            markerOptions.position(latLng);
            mOtherMarker = mGoogleMap.addMarker(markerOptions);
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

    private void addMarkerToGoogleMap(double lat, double lng) {
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            MarkerOptions mMarkerOptions = new MarkerOptions();
            mMarkerOptions.position(new LatLng(lat, lng));
            mMarkerOptions.icon(bitmapDescriptorFromVector(getBaseContext(), R.drawable.ic_location_icon));
            String addressName = getNameLocationFromLatLng(lat, lng);
            mMarkerOptions.title(addressName);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 17));
            mYourMarker = mGoogleMap.addMarker(mMarkerOptions);
            mYourMarker.showInfoWindow();
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(GoogleMapActivity.this));
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    // Checks whether two providers are the same
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(GoogleMapActivity.this));
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        mGoogleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(GoogleMapActivity.this));

        Log.d("TAGGG", "onInfoWindowClick: ");
        marker.showInfoWindow();
    }
}
