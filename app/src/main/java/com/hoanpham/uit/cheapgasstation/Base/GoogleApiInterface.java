package com.hoanpham.uit.cheapgasstation.Base;

import android.database.Observable;

import com.hoanpham.uit.cheapgasstation.Server.Direction.model.DirectionResults;
import com.hoanpham.uit.cheapgasstation.Server.Direction.model.Route;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiInterface {

    @GET(GoogleApiConfig.GOOGLE_API_DIRECTION)
    Observable<DirectionResults> getDirectionWithTwoPoint(@Query("origin") String origin,
                                                          @Query("destination") String destination,
                                                          @Query("waypoints") String waypoints,
                                                          @Query("key") String key);

}
