package com.example.eventplanner.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.eventplanner.model.utils.UserRole;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser implements Parcelable, Serializable{
    protected Long id;
    protected String email;
    protected String password;
    protected UserRole userRole;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String phoneNumber;
    protected List<BaseUser> blockedUsers;

    protected BaseUser(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        email = in.readString();
        password = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        address = in.readString();
        phoneNumber = in.readString();
        blockedUsers = in.createTypedArrayList(BaseUser.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(address);
        dest.writeString(phoneNumber);
        dest.writeTypedList(blockedUsers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseUser> CREATOR = new Creator<BaseUser>() {
        @Override
        public BaseUser createFromParcel(Parcel in) {
            return new BaseUser(in);
        }

        @Override
        public BaseUser[] newArray(int size) {
            return new BaseUser[size];
        }
    };
}
