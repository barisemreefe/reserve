package com.bee.reserve.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Generated;
import io.objectbox.annotation.Id;

/**
 * Created by barisemreefe on 09/03/2017.
 */
@Entity
public final class Table implements Parcelable{
    @Id(assignable = true)
    private long id;
    private boolean reserved;


    

    @Generated(hash = 752389689)
    public Table() {
    }


    protected Table(Parcel in) {
        id = in.readLong();
        reserved = in.readByte() != 0;
    }


    @Generated(hash = 1098472283)
    public Table(long id, boolean reserved) {
        this.id = id;
        this.reserved = reserved;
    }

    public static final Creator<Table> CREATOR = new Creator<Table>() {
        @Override
        public Table createFromParcel(Parcel in) {
            return new Table(in);
        }

        @Override
        public Table[] newArray(int size) {
            return new Table[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeByte((byte) (reserved ? 1 : 0));
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public boolean getReserved() {
        return reserved;
    }


    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", reserved=" + reserved +
                '}';
    }

}
