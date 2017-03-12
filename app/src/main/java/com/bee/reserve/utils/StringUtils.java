package com.bee.reserve.utils;

import android.text.TextUtils;

import com.bee.reserve.datatypes.Customer;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class StringUtils {
    private StringUtils() {
        throw new UnsupportedOperationException();
    }
    public static String getCustomerName(Customer customer) {
        if (customer == null) {
            return "";
        } else {
            String name ="";
            if (!TextUtils.isEmpty(customer.getFirstName())) {
                name += customer.getFirstName();
            }
            if (!TextUtils.isEmpty(customer.getLastName())) {
                name = name +" " + customer.getLastName();
            }
            return name;
        }
    }
}
