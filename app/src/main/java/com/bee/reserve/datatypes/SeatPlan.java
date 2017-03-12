package com.bee.reserve.datatypes;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Generated;

/**
 * Created by barisemreefe on 09/03/2017.
 */
@Entity
public class SeatPlan {
    public static final int UNKNOWN_CUSTOMER = 999999;
    private long tableId;
    private long customerId;
    @Id(assignable = true)
    private long id;

    public SeatPlan(long tableId, long customerId) {
        this.tableId = tableId;
        this.customerId = customerId;
    }

    @Generated(hash = 749960677)
    public SeatPlan(long tableId, long customerId, long id) {
        this.tableId = tableId;
        this.customerId = customerId;
        this.id = id;
    }

    @Generated(hash = 777164167)
    public SeatPlan() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "SeatPlan{" +
                "tableId=" + tableId +
                ", customerId=" + customerId +
                ", id=" + id +
                '}';
    }
}
