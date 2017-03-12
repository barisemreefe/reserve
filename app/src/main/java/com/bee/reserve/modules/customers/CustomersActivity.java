package com.bee.reserve.modules.customers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.bee.reserve.R;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.datatypes.SeatPlan;
import com.bee.reserve.datatypes.SeatPlanWrapper;
import com.bee.reserve.providers.CustomerProvider;
import com.bee.reserve.providers.SeatPlanProvider;
import com.bee.reserve.utils.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class CustomersActivity extends BaseActivity {
    private static final String TAG = "CustomersActivity";
    public static final String TABLE_POSITION = "tablePosition";
    public static final String CUSTOMER = "customer";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.customers_recyclerview)
    RecyclerView recyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.customers_edittext_search)
    EditText searchEditText;
    @BindColor(R.color.white)
    int white;
    @BindColor(R.color.red)
    int red;
    private int tablePosition = -1;
    private List<SeatPlanWrapper> seatPlanWrapperList;
    private List<SeatPlanWrapper> filteredSeatPlanWrapperList;
    private CustomersAdapter adapter;
    private SeatPlanProvider seatPlanProvider;
    private CustomerProvider customerProvider;

    public static Intent newIntent(Context context) {
        return new Intent(context, CustomersActivity.class);
    }

    public static Intent newIntent(Context context, int tablePosition) {
        Intent intent = new Intent(context, CustomersActivity.class);
        intent.putExtra(TABLE_POSITION, tablePosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
        ButterKnife.bind(this);
        seatPlanProvider = new SeatPlanProvider();
        customerProvider = new CustomerProvider();
        findViewById(R.id.root).requestFocus();
        tablePosition = getIntent().getIntExtra(TABLE_POSITION, -1);
        showCustomers();
    }

    private List<SeatPlanWrapper> filter(List<SeatPlanWrapper> models, String query) {
        query = query.toLowerCase();
        final List<SeatPlanWrapper> filteredModelList = new ArrayList<>();
        for (SeatPlanWrapper model : models) {
            final String firstName = model.getCustomer().getFirstName().toLowerCase();
            final String lastName = model.getCustomer().getFirstName().toLowerCase();
            if (firstName.contains(query) || lastName.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void showCustomers() {
        seatPlanWrapperList = new ArrayList<>();
        filteredSeatPlanWrapperList = new ArrayList<>();
        final List<SeatPlan> seatPlans = seatPlanProvider.getCustomersSeatPlan();
        for (Customer customer : customerProvider.getCustomers()) {
            SeatPlanWrapper seatPlanWrapper = new SeatPlanWrapper(getSeatPlanForCustomer(seatPlans, customer), customer);
            seatPlanWrapperList.add(seatPlanWrapper);
            filteredSeatPlanWrapperList.add(seatPlanWrapper);
        }
        adapter = new CustomersAdapter(this, filteredSeatPlanWrapperList, customerListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

    private SeatPlan getSeatPlanForCustomer(List<SeatPlan> seatPlans, Customer customer) {
        for (SeatPlan plan : seatPlans) {
            if (plan.getCustomerId() == customer.getId()) {
                return plan;
            }
        }
        return null;
    }

    private void showWarning(final Customer customer) {
        new AlertDialog.Builder(CustomersActivity.this)
                .setTitle(getString(R.string.warning))
                .setMessage(String.format(getString(R.string.user_already_seated),customer.getFirstName()))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSelectedCustomer(customer);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void sendSelectedCustomer(Customer customer) {
        final Intent intent = new Intent();
        intent.putExtra(CUSTOMER, customer);
        setResult(RESULT_OK, intent);
        intent.putExtra(TABLE_POSITION, tablePosition);
        finish();
    }

    @OnTextChanged(R.id.customers_edittext_search)
    void search() {
        if (searchEditText.isFocused()) {
            String query = searchEditText.getText().toString();
            final List<SeatPlanWrapper> filteredModelList = filter(seatPlanWrapperList, query);
            adapter.animateTo(filteredModelList);
            recyclerView.scrollToPosition(0);
        }
    }



    private final CustomersAdapter.CustomerListener customerListener = new CustomersAdapter.CustomerListener() {
        @Override
        public void onClick(final Customer customer, final SeatPlan seatPlan) {
            if (seatPlan == null) {
                sendSelectedCustomer(customer);
            } else {
                showWarning(customer);
            }
        }
    };



}
