package com.hoanpham.uit.cheapgasstation.Base;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.hoanpham.uit.cheapgasstation.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mWindowView;
    private Context context;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context;
        mWindowView = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null);
    }

    private void renderView(Marker marker, View view) {
        TextView mAddressName = view.findViewById(R.id.textNameAddress);
        String title = marker.getTitle();
        if (title == null || title.equals("")) {
            title = getNameLocationFromLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        }

        Log.d("TAGGG", "renderView: " + title);
        mAddressName.setText(title);
    }

    private String getNameLocationFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        try {
            List<Address> mAddressList = geocoder.getFromLocation(lat, lng, 1);
            address = mAddressList.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderView(marker, mWindowView);
        return mWindowView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderView(marker, mWindowView);
        return mWindowView;
    }
}
