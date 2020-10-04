package com.hoanpham.uit.cheapgasstation.Base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleApiClient {

    private static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private static Retrofit builder = new Retrofit.Builder()
            .baseUrl(GoogleApiConfig.GOOGLE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(getHttpClient())
            .build();

    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }


    public static <S> S createService(Class<S> serviceClass) {
        return builder.create(serviceClass);
    }

}
