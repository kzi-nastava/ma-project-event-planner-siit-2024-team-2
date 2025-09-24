package com.example.eventplanner.dto.serviceproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.dto.event.EventTypeDto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductSummaryDto implements Parcelable, Serializable {
    protected long id;
    protected ServiceProductCategoryDto category;
    protected boolean available;
    protected double price;
    protected double discount;
    protected String name;
    protected String description;
    protected String creatorEmail;
    protected String creatorName;
    protected boolean favorite;

    protected ServiceProductSummaryDto(Parcel in) {
        id = in.readLong();
        available = in.readByte() != 0;
        price = in.readDouble();
        discount = in.readDouble();
        name = in.readString();
        description = in.readString();
        creatorEmail = in.readString();
        creatorName = in.readString();
    }

    public static final Creator<ServiceProductSummaryDto> CREATOR = new Creator<ServiceProductSummaryDto>() {
        @Override
        public ServiceProductSummaryDto createFromParcel(Parcel in) {
            return new ServiceProductSummaryDto(in);
        }

        @Override
        public ServiceProductSummaryDto[] newArray(int size) {
            return new ServiceProductSummaryDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(creatorEmail);
        dest.writeString(creatorName);
    }
}
