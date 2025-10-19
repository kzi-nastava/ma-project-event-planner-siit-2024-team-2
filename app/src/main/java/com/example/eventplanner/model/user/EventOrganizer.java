package com.example.eventplanner.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.model.utils.UserRole;

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
public class EventOrganizer extends BaseUser implements Parcelable, Serializable {
    private List<ServiceProduct> favoriteServiceProducts;

    protected EventOrganizer(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        image = in.readString();
        imageEncodedName = in.readString();
        mutedNotifications = in.readByte() != 0;
        favoriteServiceProducts = in.createTypedArrayList(ServiceProduct.CREATOR);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(image);
        dest.writeString(imageEncodedName);
        dest.writeByte((byte) (mutedNotifications ? 1 : 0));
        dest.writeTypedList(favoriteServiceProducts);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventOrganizer> CREATOR = new Creator<EventOrganizer>() {
        @Override
        public EventOrganizer createFromParcel(Parcel in) {
            return new EventOrganizer(in);
        }

        @Override
        public EventOrganizer[] newArray(int size) {
            return new EventOrganizer[size];
        }
    };
}
