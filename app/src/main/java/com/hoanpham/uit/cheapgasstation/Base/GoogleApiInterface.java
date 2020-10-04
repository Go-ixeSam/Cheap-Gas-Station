package com.hoanpham.uit.cheapgasstation.Base;


import com.hoanpham.uit.cheapgasstation.Server.Direction.model.DirectionResults;
import com.hoanpham.uit.cheapgasstation.Server.NearbyPlace.NearbyResults;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiInterface {

    @GET(GoogleApiConfig.GOOGLE_API_DIRECTION)
    Single<DirectionResults> getDirectionWithTwoPoint(@Query("origin") String origin,
                                                      @Query("destination") String destination,
//                                                      @Query("waypoints") String waypoints,
                                                      @Query("travel_mode") String mode,
                                                      @Query("key") String key);

   @GET(GoogleApiConfig.GOOGLE_API_NEARBY_PLACE)
    Single<NearbyResults> getNearByPlace(@Query("location") String originLocation,
                                         @Query("radius") String radius,
                                         @Query("type") String type,
                                         @Query("key") String key);

   @GET(GoogleApiConfig.GOOGLE_API_DIRECTION)
    Single<DirectionResults> getDirectionMultiPoint(@Query("origin") String origin,
                                                    @Query("destination") String destination,
                                                    @Query("waypoints") String waypoints,
                                                    @Query("travel_mode") String mode,
                                                    @Query("key") String key);

}
