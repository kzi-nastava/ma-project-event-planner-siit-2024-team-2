package com.example.eventplanner.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.model.utils.UserRole;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductProvider extends BaseUser implements Parcelable, Serializable {
    private String companyName;
    private String companyDescription;
    private List<ServiceProductCategory> serviceProductCategory = new ArrayList<>();

    protected ServiceProductProvider(Parcel in) {
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
        companyName = in.readString();
        companyDescription = in.readString();
    }

    public static final Creator<ServiceProductProvider> CREATOR = new Creator<ServiceProductProvider>() {
        @Override
        public ServiceProductProvider createFromParcel(Parcel in) {
            return new ServiceProductProvider(in);
        }

        @Override
        public ServiceProductProvider[] newArray(int size) {
            return new ServiceProductProvider[size];
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
        dest.writeString(email);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeString(image);
        dest.writeString(imageEncodedName);
        dest.writeByte((byte) (mutedNotifications ? 1 : 0));
        dest.writeString(companyName);
        dest.writeString(companyDescription);
    }
}
