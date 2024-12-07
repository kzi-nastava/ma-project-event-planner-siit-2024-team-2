package com.example.eventplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("event")
    @Expose
    private Event event;

    @SerializedName("service")
    @Expose
    private Service service;

    @SerializedName("price")
    @Expose
    private double price;

    @SerializedName("date")
    @Expose
    private Date date;

    @SerializedName("duration")
    @Expose
    private double duration;

    protected Booking(Parcel in) {
        id = in.readLong();
        event = in.readParcelable(Event.class.getClassLoader());
        service = in.readParcelable(Service.class.getClassLoader());
        price = in.readDouble();
        date = new Date(in.readLong());
        duration = in.readDouble();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeParcelable((Parcelable) event, flags);
        dest.writeParcelable(service, flags);
        dest.writeDouble(price);
        dest.writeLong(date.getTime());
        dest.writeDouble(duration);
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
}
