package com.bee.reserve.utils;

import android.app.Application;

import com.bee.reserve.datatypes.MyObjectBox;
import com.bee.reserve.network.ReserveApi;
import com.bee.reserve.network.ReserveRestClient;
import com.squareup.leakcanary.LeakCanary;

import io.objectbox.BoxStore;

/**
 * Created by barisemreefe on 09/03/2017.
 */

public class ReserveApplication extends Application {
    private static ReserveApi reserveApi;
    private static ReserveRestClient reserveRestClient;
    private static BoxStore boxStore;
    @Override
    public void onCreate() {
        super.onCreate();
        initializeLeakCanary();
        reserveRestClient = new ReserveRestClient(this);
        boxStore = MyObjectBox.builder().androidContext(this).build();

    }
    private void initializeLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
    public static BoxStore getBoxStore() {
        return boxStore;
    }

    public static ReserveApi getApi() {
        if(reserveApi == null) {
            reserveApi = reserveRestClient.getApi();
        }
        return reserveApi;
    }
}
