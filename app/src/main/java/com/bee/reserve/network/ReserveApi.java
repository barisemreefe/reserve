package com.bee.reserve.network;

import com.bee.reserve.datatypes.Customer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by barisemreefe on 09/03/2017.
 */

public interface ReserveApi {
    @GET("quandoo-assessment/table-map.json")
    Call<List<Boolean>> getTableInfo();
    @GET("quandoo-assessment/customer-list.json")
    Call<List<Customer>> getCustomerList();
}
