package com.bee.reserve.providers;

import com.bee.reserve.utils.ReserveApplication;
import com.bee.reserve.datatypes.Customer;
import com.bee.reserve.datatypes.SeatPlan;
import com.bee.reserve.datatypes.SeatPlan_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class SeatPlanProvider {
    private static Box<SeatPlan> seatPlanBox;
    private Query<SeatPlan> seatPlanQuery;

    private Box<SeatPlan> getSeatPlanBox() {
        if (seatPlanBox == null) {
            seatPlanBox = ReserveApplication.getBoxStore().boxFor(SeatPlan.class);
        }
        return seatPlanBox;
    }
    public List<SeatPlan> getCustomersSeatPlan() {
        if (seatPlanQuery == null) {
            seatPlanQuery = getSeatPlanBox().query().greater(SeatPlan_.customerId, 0).build();
        }
        return seatPlanQuery.find();
    }
    public List<SeatPlan> getSeatPlan() {
        return getSeatPlanBox().getAll();
    }
    public int size() {
        long seatPlanCount = getSeatPlanBox().count();
        return (int)seatPlanCount;
    }
    public void put(SeatPlan seatPlan) {
        getSeatPlanBox().put(seatPlan);
    }
    public void updateSeatPlan(long seatPlanId, long customerId) {
        getSeatPlanBox().remove(seatPlanId);
        getSeatPlanBox().put(new SeatPlan(seatPlanId, customerId, seatPlanId));
    }
    public SeatPlan getCustomersSeatPlan(Customer customer) {
        return getSeatPlanBox().query().equal(SeatPlan_.customerId,customer.getId()).build().findFirst();
    }
    public void clear() {
        getSeatPlanBox().removeAll();
    }
}
