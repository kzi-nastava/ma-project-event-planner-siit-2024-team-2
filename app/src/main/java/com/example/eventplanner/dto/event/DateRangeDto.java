package com.example.eventplanner.dto.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeDto implements Parcelable, Serializable {
    private Long start;
    private Long end;

    protected DateRangeDto(Parcel in) {
        if (in.readByte() == 0) {
            start = null;
        } else {
            start = in.readLong();
        }
        if (in.readByte() == 0) {
            end = null;
        } else {
            end = in.readLong();
        }
    }

    public static final Creator<DateRangeDto> CREATOR = new Creator<DateRangeDto>() {
        @Override
        public DateRangeDto createFromParcel(Parcel in) {
            return new DateRangeDto(in);
        }

        @Override
        public DateRangeDto[] newArray(int size) {
            return new DateRangeDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (start == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(start);
        }
        if (end == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(end);
        }
    }
}
