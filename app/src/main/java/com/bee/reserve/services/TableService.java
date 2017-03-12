package com.bee.reserve.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bee.reserve.utils.ReserveApplication;
import com.bee.reserve.datatypes.Table;
import com.bee.reserve.providers.TableProvider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TableService extends IntentService {
    private static final String TAG = "TableService";
    public static final String ACTION_GET_TABLE_INFO = "getTableInfo";
    public static final String TABLES = "tables";
    private LocalBroadcastManager localBroadcastManager;
    private Intent resultIntent;
    private TableProvider tableProvider;

    public TableService() {
        super(TAG);
    }

    public static void getTables(Context context) {
        Intent intent = new Intent(context, TableService.class);
        intent.setAction(ACTION_GET_TABLE_INFO);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_TABLE_INFO.equals(action)) {
                handleActionGetTableInfo();
            }
        }
    }

    private void handleActionGetTableInfo() {
        tableProvider = new TableProvider();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        resultIntent = new Intent();
        resultIntent.setAction(ACTION_GET_TABLE_INFO);
        final ServiceStatus serviceStatus = new ServiceStatus(
                ServiceStatus.Status.UNSYNCED,
                ServiceStatus.Type.TABLE_INFO);
        resultIntent.putExtra(ServiceStatus.TAG,serviceStatus);
        final ArrayList<Table> tables = new ArrayList<>();
        tables.addAll(tableProvider.getTables());
        resultIntent.putExtra(TABLES,tables);
        localBroadcastManager.sendBroadcast(resultIntent);
        getTableInfo();
    }
    private void getTableInfo() {
        Call<List<Boolean>> tableInfoCall = ReserveApplication.getApi().getTableInfo();
        tableInfoCall.enqueue(tableInfoCallback);
    }


    private final Callback<List<Boolean>> tableInfoCallback = new Callback<List<Boolean>>() {
        @Override
        public void onResponse(Call<List<Boolean>> call, Response<List<Boolean>> response) {

            if (response.isSuccessful()) {

                for (int i = 0; i < response.body().size(); i++) {
                    int dbIndex = i+1;
                    Boolean occupancy = response.body().get(i);
                    if (response.body().size()>tableProvider.size()) {
                        tableProvider.put(new Table(dbIndex,occupancy));
                    }
                }
                final ServiceStatus serviceStatus = new ServiceStatus(
                        ServiceStatus.Status.SYNCED,
                        ServiceStatus.Type.TABLE_INFO);
                resultIntent.putExtra(ServiceStatus.TAG,serviceStatus);
                final ArrayList<Table> tables = new ArrayList<>();
                tables.addAll(tableProvider.getTables());
                resultIntent.putExtra(TABLES,tables);
                localBroadcastManager.sendBroadcast(resultIntent);
            }

        }

        @Override
        public void onFailure(Call<List<Boolean>> call, Throwable t) {
            Log.d(TAG, t.getLocalizedMessage());
        }
    };

}
