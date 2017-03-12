package com.bee.reserve.modules.customers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bee.reserve.R;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.datatypes.SeatPlan;
import com.bee.reserve.datatypes.SeatPlanWrapper;
import com.bee.reserve.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by barisemreefe on 09/03/2017.
 */

public class CustomersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomersAdapter";
    private final List<SeatPlanWrapper> seatPlanWrapperList;
    private final Context context;
    private final CustomerListener listener;
    private final Drawable occupied, vacant;

    public interface CustomerListener {
        void onClick(Customer customer, SeatPlan seatPlan);
    }

    public CustomersAdapter(Context context, List<SeatPlanWrapper> seatPlanWrapperList, CustomerListener listener) {
        this.seatPlanWrapperList = seatPlanWrapperList;
        this.context = context;
        this.listener = listener;
        occupied = ContextCompat.getDrawable(context, R.drawable.ic_restaurant_occupied);
        vacant = ContextCompat.getDrawable(context, R.drawable.ic_restaurant_vacant_24dp);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_customer, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                    final SeatPlanWrapper seatPlanWrapper = seatPlanWrapperList.get(vh.getAdapterPosition());
                    final Customer customer = seatPlanWrapper.getCustomer();
                    final SeatPlan seatPlan = seatPlanWrapper.getSeatPlan();
                    listener.onClick(customer, seatPlan);
                }
            }
        });

        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindCustomer((ViewHolder) holder, seatPlanWrapperList.get(position));
    }

    private void bindCustomer(ViewHolder holder, SeatPlanWrapper seatPlanWrapper) {
        final Customer customer = seatPlanWrapper.getCustomer();
        final SeatPlan seatPlan = seatPlanWrapper.getSeatPlan();
        String status = (seatPlan != null) ?
                String.format(context.getString(R.string.sitting_at_table), seatPlan.getTableId()) :
                "";
        holder.nameTextView.setText(StringUtils.getCustomerName(customer));
        holder.statusTextView.setText(status);
        holder.statusTextView.setVisibility((seatPlan != null) ? View.VISIBLE : View.GONE);
        holder.statusImageView.setImageDrawable(seatPlan != null ? occupied : vacant);
    }

    public void animateTo(List<SeatPlanWrapper> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<SeatPlanWrapper> newModels) {
        for (int i = seatPlanWrapperList.size() - 1; i >= 0; i--) {
            final SeatPlanWrapper model = seatPlanWrapperList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<SeatPlanWrapper> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final SeatPlanWrapper model = newModels.get(i);
            if (!seatPlanWrapperList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<SeatPlanWrapper> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final SeatPlanWrapper model = newModels.get(toPosition);
            final int fromPosition = seatPlanWrapperList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private void removeItem(int position) {
        final SeatPlanWrapper model = seatPlanWrapperList.remove(position);
        notifyItemRemoved(position);
    }

    private void addItem(int position, SeatPlanWrapper model) {
        seatPlanWrapperList.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final SeatPlanWrapper model = seatPlanWrapperList.remove(fromPosition);
        seatPlanWrapperList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return seatPlanWrapperList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customer_textview_name)
        TextView nameTextView;
        @BindView(R.id.customer_textview_status)
        TextView statusTextView;
        @BindView(R.id.customer_imageview_status)
        AppCompatImageView statusImageView;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}




