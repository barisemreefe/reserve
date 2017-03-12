package com.bee.reserve.providers;

import com.bee.reserve.utils.ReserveApplication;
import com.bee.reserve.datatypes.Customer;

import java.util.List;

import io.objectbox.Box;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class CustomerProvider {
    private static Box<Customer> customerBox;

    private Box<Customer> getCustomerBox() {
        if (customerBox == null) {
            customerBox = ReserveApplication.getBoxStore().boxFor(Customer.class);
        }
        return customerBox;
    }

    public List<Customer> getCustomers() {
        return getCustomerBox().getAll();
    }

    public void clear() {
        getCustomerBox().removeAll();
    }

    public void put(Customer customer) {
        getCustomerBox().put(customer);
    }

    public Customer getCustomer(long customerId) {
        return getCustomerBox().get(customerId);
    }
}
