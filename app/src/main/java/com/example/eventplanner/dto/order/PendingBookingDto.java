package com.example.eventplanner.dto.order;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.serviceproduct.Service;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingBookingDto implements Parcelable, Serializable {
    private Long id;
    private Service service;
    private double price;
    private Long date;
    private double duration;
    private Instant createdAt;
    private String bookerName;
    private String bookerEmail;
    private boolean hiding;
    private boolean hidden;

    protected PendingBookingDto(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        service = in.readParcelable(Service.class.getClassLoader());
        price = in.readDouble();
        date = in.readLong();
        duration = in.readDouble();
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt == -1 ? null : Instant.ofEpochMilli(tmpCreatedAt);
        bookerName = in.readString();
        bookerEmail = in.readString();
        hiding = in.readByte() != 0;
        hidden = in.readByte() != 0;
    }

    public static final Creator<PendingBookingDto> CREATOR = new Creator<PendingBookingDto>() {
        @Override
        public PendingBookingDto createFromParcel(Parcel in) {
            return new PendingBookingDto(in);
        }

        @Override
        public PendingBookingDto[] newArray(int size) {
            return new PendingBookingDto[size];
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
        dest.writeParcelable(service, flags);
        dest.writeDouble(price);
        dest.writeLong(date != null ? date : -1);
        dest.writeDouble(duration);
        dest.writeLong(createdAt != null ? createdAt.toEpochMilli() : -1);
        dest.writeString(bookerName);
        dest.writeString(bookerEmail);
        dest.writeByte((byte) (hiding ? 1 : 0));
        dest.writeByte((byte) (hidden ? 1 : 0));
    }
}
