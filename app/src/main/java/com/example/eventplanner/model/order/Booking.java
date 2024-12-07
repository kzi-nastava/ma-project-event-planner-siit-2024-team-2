package com.example.eventplanner.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.serviceproduct.Service;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Parcelable, Serializable {
    private Long id;
    private Event event;
    private Service service;
    private double price;
    private Date date;
    private double duration;

    protected Booking(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        event = in.readParcelable(Event.class.getClassLoader());
        service = in.readParcelable(Service.class.getClassLoader());
        price = in.readDouble();
        duration = in.readDouble();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
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
        dest.writeParcelable(event, flags);
        dest.writeParcelable(service, flags);
        dest.writeDouble(price);
        dest.writeDouble(duration);
    }
}
