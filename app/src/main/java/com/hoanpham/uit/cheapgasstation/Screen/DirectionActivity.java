package com.hoanpham.uit.cheapgasstation.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiClient;
import com.hoanpham.uit.cheapgasstation.Base.GoogleApiInterface;
import com.hoanpham.uit.cheapgasstation.R;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentGoogleMapSupport);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleMap != null){
            GoogleApiInterface apiInterface = GoogleApiClient.createService(GoogleApiInterface.class);
//            apiInterface.getDirectionWithTwoPoint()
        }
    }
}
