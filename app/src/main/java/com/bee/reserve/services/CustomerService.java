package com.bee.reserve.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bee.reserve.utils.ReserveApplication;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.providers.CustomerProvider;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerService extends IntentService {
    private static final String TAG = "CustomerService";
    public static final String ACTION_GET_CUSTOMERS = "getCustomers";
    private static final String CUSTOMERS = "customers";
    private LocalBroadcastManager localBroadcastManager;
    private Intent resultIntent;
    private CustomerProvider customerProvider;

    public CustomerService() {
        super(TAG);
    }

    public static void getCustomers(Context context) {
        Intent intent = new Intent(context, CustomerService.class);
        intent.setAction(ACTION_GET_CUSTOMERS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_CUSTOMERS.equals(action)) {
                handleActionGetCustomers();
            }
        }
    }

    private void handleActionGetCustomers() {
        customerProvider = new CustomerProvider();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        resultIntent = new Intent();
        resultIntent.setAction(ACTION_GET_CUSTOMERS);
        final ServiceStatus serviceStatus = new ServiceStatus(
                ServiceStatus.Status.UNSYNCED,
                ServiceStatus.Type.CUSTOMER_INFO);
        resultIntent.putExtra(ServiceStatus.TAG,serviceStatus);
        final ArrayList<Customer> customers = new ArrayList<>();
        customers.addAll(customerProvider.getCustomers());
        resultIntent.putExtra(CUSTOMERS,customers);
        localBroadcastManager.sendBroadcast(resultIntent);
        getCustomerList();
    }

    private void getCustomerList() {
        Call<List<Customer>> customerListCall = ReserveApplication.getApi().getCustomerList();
        customerListCall.enqueue(customerResponseCallback);
    }
    private final Callback<List<Customer>> customerResponseCallback = new Callback<List<Customer>>() {
        @Override
        public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
            if(response.isSuccessful()) {
                customerProvider.clear();
                for (int i = 0; i < response.body().size(); i++) {
                    int dbIndex = i+1;
                    Customer customer = response.body().get(i);
                    customer.setId(dbIndex);
                    customerProvider.put(customer);
                }
                final ServiceStatus serviceStatus = new ServiceStatus(
                        ServiceStatus.Status.SYNCED,
                        ServiceStatus.Type.CUSTOMER_INFO);
                resultIntent.putExtra(ServiceStatus.TAG,serviceStatus);
                final ArrayList<Customer> customers = new ArrayList<>();
                customers.addAll(customerProvider.getCustomers());
                resultIntent.putExtra(CUSTOMERS,customers);
                localBroadcastManager.sendBroadcast(resultIntent);
            }

        }

        @Override
        public void onFailure(Call<List<Customer>> call, Throwable t) {
            Log.d(TAG, "t " + t.getLocalizedMessage());
        }
    };

}

