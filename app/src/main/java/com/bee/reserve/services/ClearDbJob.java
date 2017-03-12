package com.bee.reserve.services;

import com.bee.reserve.providers.CustomerProvider;
import com.bee.reserve.providers.SeatPlanProvider;
import com.bee.reserve.providers.TableProvider;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by barisemreefe on 12/03/2017.
 */

public class ClearDbJob extends JobService {
    public static final String TAG = "ClearDbJob";

    @Override
    public boolean onStartJob(JobParameters job) {
        SeatPlanProvider seatPlanProvider = new SeatPlanProvider();
        CustomerProvider customerProvider = new CustomerProvider();
        TableProvider tableProvider = new TableProvider();
        seatPlanProvider.clear();
        customerProvider.clear();
        tableProvider.clear();
        return false;
    }
    
    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
    
}

