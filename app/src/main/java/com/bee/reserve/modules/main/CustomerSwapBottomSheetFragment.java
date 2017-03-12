package com.bee.reserve.modules.main;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.TextView;

import com.bee.reserve.R;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class CustomerSwapBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String CUSTOMER = "customer";
    private static final String POSITION = "position";
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.customerswap_textview_name)
    TextView nameTextView;
    private int position;

    private CustomerSwapListener listener;

    public interface CustomerSwapListener {
        void onDeleteSelected(int position);

        void onSwapSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (CustomerSwapListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() + " CustomerSwapListener");
        }
    }

    public static CustomerSwapBottomSheetFragment newInstance(Customer customer, int position) {
        Bundle args = new Bundle();
        args.putParcelable(CUSTOMER, customer);
        args.putInt(POSITION, position);
        CustomerSwapBottomSheetFragment fragment = new CustomerSwapBottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_customer_swap, null);
        dialog.setContentView(contentView);
        ButterKnife.bind(this, contentView);

        final Bundle bundle = getArguments();
        if (bundle != null && bundle.getParcelable(CUSTOMER) != null) {
            Customer customer = bundle.getParcelable(CUSTOMER);
            position = bundle.getInt(POSITION);
            nameTextView.setText(StringUtils.getCustomerName(customer));
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(bottomSheetBehaviorCallback);
        }
    }

    @OnClick(R.id.customerswap_textview_delete)
    void delete() {
        listener.onDeleteSelected(position);
    }

    @OnClick(R.id.customerswap_textview_swap)
    void swap() {
        listener.onSwapSelected(position);
    }


    private final BottomSheetBehavior.BottomSheetCallback bottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };
}
