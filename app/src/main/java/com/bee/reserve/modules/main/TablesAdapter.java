package com.bee.reserve.modules.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bee.reserve.R;
import com.bee.reserve.datatypes.SeatPlan;
import com.bee.reserve.datatypes.SeatPlanWrapper;
import com.bee.reserve.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by barisemreefe on 09/03/2017.
 */

class TablesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TablesAdapter";
    private final List<SeatPlanWrapper> seatPlans;
    private final Context context;
    private final TablesAdapterListener listener;
    private final Drawable occupied,vacant;
    private final String occupiedText;

    interface TablesAdapterListener {
        void onTableSelected(int position);
    }

    public TablesAdapter(Context context, List<SeatPlanWrapper> seatPlans, TablesAdapterListener listener) {
        this.seatPlans = seatPlans;
        this.context = context;
        this.listener = listener;
        occupied = ContextCompat.getDrawable(context,R.drawable.bg_occupied);
        vacant = ContextCompat.getDrawable(context,R.drawable.bg_vacant);
        occupiedText = context.getString(R.string.occupied);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_table, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.getAdapterPosition() != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTableSelected(vh.getAdapterPosition());
                }
            }
        });
        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindTable((ViewHolder)holder,seatPlans.get(position));
    }

    private void bindTable(ViewHolder holder, SeatPlanWrapper seatPlan) {
        if (seatPlan.getCustomer() != null) {
            holder.tableNameTextView.setText(StringUtils.getCustomerName(seatPlan.getCustomer()));
            holder.itemView.setBackgroundDrawable(occupied);
        } else if (seatPlan.getSeatPlan().getCustomerId() == SeatPlan.UNKNOWN_CUSTOMER) {
            holder.tableNameTextView.setText(occupiedText);
            holder.itemView.setBackgroundDrawable(occupied);
        }else {
            holder.tableNameTextView.setText(String.format(context.getString(R.string.table),seatPlan.getSeatPlan().getId()));
            holder.itemView.setBackgroundDrawable(vacant);
        }
    }

    @Override
    public int getItemCount() {
        return seatPlans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.table_textview_name)
        TextView tableNameTextView;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}



