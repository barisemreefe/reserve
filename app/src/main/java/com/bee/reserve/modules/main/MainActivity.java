package com.bee.reserve.modules.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bee.reserve.R;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.datatypes.SeatPlan;
import com.bee.reserve.datatypes.SeatPlanWrapper;
import com.bee.reserve.datatypes.Table;
import com.bee.reserve.modules.customers.CustomersActivity;
import com.bee.reserve.providers.CustomerProvider;
import com.bee.reserve.providers.SeatPlanProvider;
import com.bee.reserve.providers.TableProvider;
import com.bee.reserve.services.ClearDbJob;
import com.bee.reserve.services.CustomerService;
import com.bee.reserve.services.ServiceStatus;
import com.bee.reserve.services.TableService;
import com.bee.reserve.utils.BaseActivity;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements CustomerSwapBottomSheetFragment.CustomerSwapListener {
    private static final String TAG = "MainActivity";
    private static final int EXECUTION_FRAME_START = 10 * 60; //10 Mins
    private static final int EXECUTION_FRAME_END = 11 * 60; //10 Mins
    private static final int SELECT_CUSTOMER = 999;
    private static final int SELECT_CUSTOMER_WITH_TABLE = 888;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private TablesAdapter adapter;
    private LocalBroadcastManager localBroadcastManager;
    private
    @ServiceStatus.Status
    int customerStatus, tableStatus;
    private List<SeatPlanWrapper> seatPlans;
    private List<Table> tables = Collections.EMPTY_LIST;

    private Customer customerToBeSeated;
    private SeatPlanProvider seatPlanProvider;
    private TableProvider tableProvider;
    private CustomerProvider customerProvider;
    private BottomSheetDialogFragment bottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeProviders();
        initializeBroadcastManager();
        CustomerService.getCustomers(this);
        TableService.getTables(this);
        startClearDbJob();
        seatPlans = new ArrayList<>();
        adapter = new TablesAdapter(this, seatPlans, tablesAdapterListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

    }

    private void startClearDbJob() {
        final FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job job = dispatcher.newJobBuilder().setService(ClearDbJob.class)
                .setTag(ClearDbJob.TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(EXECUTION_FRAME_START,EXECUTION_FRAME_END))
                .build();
        dispatcher.schedule(job);
    }

    private void initializeProviders() {
        seatPlanProvider = new SeatPlanProvider();
        tableProvider = new TableProvider();
        customerProvider = new CustomerProvider();
    }


    private void initializeBroadcastManager() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CustomerService.ACTION_GET_CUSTOMERS);
        filter.addAction(TableService.ACTION_GET_TABLE_INFO);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceStatus serviceStatus = intent.getParcelableExtra(ServiceStatus.TAG);
            if (ServiceStatus.Type.CUSTOMER_INFO == serviceStatus.getType()) {
                customerStatus = serviceStatus.getStatus();
            } else if (ServiceStatus.Type.TABLE_INFO == serviceStatus.getType()) {
                tableStatus = serviceStatus.getStatus();
                tables = intent.getParcelableArrayListExtra(TableService.TABLES);
            }
            initializeSeatPlan();

        }

    };

    private void initializeSeatPlan() {
        if (tableStatus == customerStatus) {
            final int seatPlanCount = seatPlanProvider.size();
            for (int i = Math.max(0, seatPlanCount - 1); i < tables.size(); i++) {
                Table table = tables.get(i);
                seatPlanProvider.put(new SeatPlan(table.getId(), (table.getReserved())?SeatPlan.UNKNOWN_CUSTOMER:0));
            }

            seatPlans.clear();
            List<SeatPlan> dbSeatPlans = seatPlanProvider.getSeatPlan();
            for (SeatPlan dbSeatPlan : dbSeatPlans) {
                seatPlans.add(new SeatPlanWrapper(dbSeatPlan,
                        (dbSeatPlan.getCustomerId() == 0) ? null : customerProvider.getCustomer(dbSeatPlan.getCustomerId()))
                );
            }

            adapter.notifyDataSetChanged();

        }
    }

    @NonNull
    private void placeCustomer(int position) {
        SeatPlanWrapper seatPlan = seatPlans.get(position);
        seatPlan.setCustomer(customerToBeSeated);
        removeCustomer(customerToBeSeated);
        seatPlanProvider.updateSeatPlan(seatPlan.getSeatPlan().getId(),customerToBeSeated.getId());
        seatPlans.set(position, seatPlan);
        adapter.notifyItemChanged(position);
        tableProvider.updateTable(seatPlan.getSeatPlan().getTableId(),true);
        customerToBeSeated = null;
    }
    private void removeCustomer(Customer customer) {
        SeatPlan seatPlan = seatPlanProvider.getCustomersSeatPlan(customer);
        if (seatPlan!=null) {
            int position = findSeatPlanPosition(seatPlan);
            if (position != -1) {
                seatPlans.get(position).setCustomer(null);
                adapter.notifyItemChanged(position);
                seatPlanProvider.updateSeatPlan(seatPlan.getId(),0);
            }
        }
    }
    private int findSeatPlanPosition(SeatPlan plan) {
        for (int i = 0; i < seatPlans.size(); i++) {
            SeatPlan seatPlan = seatPlans.get(i).getSeatPlan();
            if (seatPlan.getId() == plan.getId()) {
                return i;
            }
        }
        return -1;
    }

    @OnClick(R.id.main_fab)
    void showCustomers() {
        startActivityForResult(CustomersActivity.newIntent(this), SELECT_CUSTOMER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode && (SELECT_CUSTOMER == requestCode || SELECT_CUSTOMER_WITH_TABLE == requestCode)) {
            int position = data.getIntExtra(CustomersActivity.TABLE_POSITION,-1);
            customerToBeSeated = data.getParcelableExtra(CustomersActivity.CUSTOMER);
            if ( position != -1) {
                placeCustomer(position);
            } else {
                Snackbar.make(findViewById(R.id.root), String.format(getString(R.string.select_a_table),customerToBeSeated.getFirstName()), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private final TablesAdapter.TablesAdapterListener tablesAdapterListener = new TablesAdapter.TablesAdapterListener() {
        @Override
        public void onTableSelected(int position) {
            final Customer customer = seatPlans.get(position).getCustomer();
            if (customerToBeSeated != null) {
                placeCustomer(position);
            } else if (customer != null) {
                bottomSheetDialogFragment = CustomerSwapBottomSheetFragment.newInstance(customer,position);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
            }
            else {
                startActivityForResult(CustomersActivity.newIntent(MainActivity.this,position), SELECT_CUSTOMER_WITH_TABLE);
            }
        }
    };

    @Override
    public void onDeleteSelected(int position) {
        Customer customer =seatPlans.get(position).getCustomer();
        removeCustomer(customer);
        bottomSheetDialogFragment.dismiss();
    }

    @Override
    public void onSwapSelected(int position) {
        startActivityForResult(CustomersActivity.newIntent(MainActivity.this,position), SELECT_CUSTOMER_WITH_TABLE);
        bottomSheetDialogFragment.dismiss();
    }
}

