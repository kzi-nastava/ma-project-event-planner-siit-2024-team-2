package com.example.eventplanner.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget implements Parcelable, Serializable {
    private Long id;
    private double plannedSpending;
    private double maxAmount;
    private ServiceProductCategory serviceProductCategory;

    protected Budget(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        plannedSpending = in.readDouble();
        maxAmount = in.readDouble();
        serviceProductCategory = in.readParcelable(ServiceProductCategory.class.getClassLoader());
    }

    public static final Creator<Budget> CREATOR = new Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeDouble(plannedSpending);
        dest.writeDouble(maxAmount);
        dest.writeParcelable(serviceProductCategory, flags);
    }
}
