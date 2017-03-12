package com.bee.reserve.network;

import android.content.Context;

import com.bee.reserve.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by barisemreefe on 09/03/2017.
 */

public class ReserveRestClient {
    private static final String TAG = "ReserveRestClient";
    private static final String BASE_URL = "https://s3-eu-west-1.amazonaws.com/";
    private final static int CONNECTION_TIMEOUT = 30;
    private final static int READ_TIMEOUT = 30;
    private final static int CACHE_SIZE = 10 * 1024 * 1024; // 10 MiB
    private final ReserveApi api;

    public ReserveRestClient(final Context context) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel((BuildConfig.DEBUG) ?
                HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .cache(new Cache(context.getCacheDir(), CACHE_SIZE))
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL).client(okHttpClient)
                .build();
        api = retrofit.create(ReserveApi.class);
    }

    public ReserveApi getApi() {
        return api;
    }
}

