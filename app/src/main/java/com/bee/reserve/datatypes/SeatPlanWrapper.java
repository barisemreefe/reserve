package com.bee.reserve.datatypes;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class SeatPlanWrapper {
    private SeatPlan seatPlan;
    private Customer customer;

    public SeatPlanWrapper(SeatPlan seatPlan, Customer customer) {
        this.seatPlan = seatPlan;
        this.customer = customer;
    }

    public SeatPlan getSeatPlan() {
        return seatPlan;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setSeatPlan(SeatPlan seatPlan) {
        this.seatPlan = seatPlan;
    }
}
