package com.example.eventplanner.model.serviceproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.ServiceProductProvider;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
public class ServiceProduct implements Parcelable, Serializable {
    protected Long id;
	protected ServiceProductCategory category;
    protected boolean available;
    protected boolean visible;
    protected double price;
    protected double discount;
    protected String name;
    protected String description;
    protected List<String> images;
    protected List<EventType> availableEventTypes;
    protected ServiceProductProvider serviceProductProvider;

    protected ServiceProduct() {}

    protected ServiceProduct(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        category = in.readParcelable(ServiceProductCategory.class.getClassLoader());
        available = in.readByte() != 0;
        visible = in.readByte() != 0;
        price = in.readDouble();
        discount = in.readDouble();
        name = in.readString();
        description = in.readString();
        images = in.createStringArrayList();
        availableEventTypes = in.createTypedArrayList(EventType.CREATOR);
        serviceProductProvider = in.readParcelable(ServiceProductProvider.class.getClassLoader());
    }

    public static final Creator<ServiceProduct> CREATOR = new Creator<ServiceProduct>() {
        @Override
        public ServiceProduct createFromParcel(Parcel in) {
            return new ServiceProduct(in);
        }

        @Override
        public ServiceProduct[] newArray(int size) {
            return new ServiceProduct[size];
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
        dest.writeParcelable(category, flags);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(images);
        dest.writeTypedList(availableEventTypes);
        dest.writeParcelable(serviceProductProvider, flags);
    }
}
