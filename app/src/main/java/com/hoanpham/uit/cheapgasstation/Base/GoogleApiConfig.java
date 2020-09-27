package com.hoanpham.uit.cheapgasstation.Base;

public class GoogleApiConfig {
    public static final String GOOGLE_API_BASE_URL = "https://maps.googleapis.com/maps/";
    public static final String GOOGLE_API_NEARBY_PLACE = "api/place/nearbysearch/";
    public static final String GOOGLE_API_DIRECTION = "api/directions/json";

    public static GoogleApiConfig mApiConfig = null;
    public static GoogleApiConfig getInstance(){
        if(mApiConfig == null){
            mApiConfig = new GoogleApiConfig();
        }

        return mApiConfig;
    }
}
