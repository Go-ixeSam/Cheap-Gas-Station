package com.hoanpham.uit.cheapgasstation.Base;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleApiClient {
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(GoogleApiConfig.GOOGLE_API_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create()).client(getHttpClient().build());

    public static OkHttpClient.Builder getHttpClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.connectTimeout(5, TimeUnit.SECONDS)
                .pingInterval(1, TimeUnit.SECONDS)
                .eventListener(new EventListener() {
                    @Override
                    public void callStart(Call call) {
                        super.callStart(call);
                    }

                    @Override
                    public void requestBodyStart(Call call) {
                        super.requestBodyStart(call);
                    }

                    @Override
                    public void requestBodyEnd(Call call, long byteCount) {
                        super.requestBodyEnd(call, byteCount);
                    }

                    @Override
                    public void responseBodyStart(Call call) {
                        super.responseBodyStart(call);
                    }

                    @Override
                    public void responseBodyEnd(Call call, long byteCount) {
                        super.responseBodyEnd(call, byteCount);
                    }

                    @Override
                    public void callEnd(Call call) {
                        super.callEnd(call);
                    }

                    @Override
                    public void callFailed(Call call, IOException ioe) {
                        super.callFailed(call, ioe);
                    }
                });
        return httpClient;
    }

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }

}
