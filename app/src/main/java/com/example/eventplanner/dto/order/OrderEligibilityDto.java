package com.example.eventplanner.dto.order;

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
public class OrderEligibilityDto implements Parcelable, Serializable {
    private boolean canOrder;
    private String reason;

    protected OrderEligibilityDto(Parcel in) {
        canOrder = in.readByte() != 0;
        reason = in.readString();
    }

    public static final Creator<OrderEligibilityDto> CREATOR = new Creator<OrderEligibilityDto>() {
        @Override
        public OrderEligibilityDto createFromParcel(Parcel in) {
            return new OrderEligibilityDto(in);
        }

        @Override
        public OrderEligibilityDto[] newArray(int size) {
            return new OrderEligibilityDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (canOrder ? 1 : 0));
        dest.writeString(reason);
    }
}
