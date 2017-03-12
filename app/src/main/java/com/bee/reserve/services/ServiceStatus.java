package com.bee.reserve.services;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by barisemreefe on 10/03/2017.
 */

public final class ServiceStatus implements Parcelable{
    public static final String TAG = "ServiceStatus";
    protected ServiceStatus(Parcel in) {
        status = in.readInt();
        type = in.readInt();
    }

    public static final Creator<ServiceStatus> CREATOR = new Creator<ServiceStatus>() {
        @Override
        public ServiceStatus createFromParcel(Parcel in) {
            return new ServiceStatus(in);
        }

        @Override
        public ServiceStatus[] newArray(int size) {
            return new ServiceStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeInt(type);
    }

    @IntDef({Status.SYNCED,Status.UNSYNCED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int SYNCED = 99;
        int UNSYNCED = 88;
    }
    @IntDef({Type.CUSTOMER_INFO,Type.TABLE_INFO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int CUSTOMER_INFO = 0;
        int TABLE_INFO = 1;
    }
    private final int status;
    private final int type;

    public ServiceStatus(@Status int status, @Type int type) {
        this.status = status;
        this.type = type;
    }

    public @Status int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }
}
