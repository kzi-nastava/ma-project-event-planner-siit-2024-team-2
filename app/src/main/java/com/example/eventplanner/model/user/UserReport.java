package com.example.eventplanner.model.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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
public class UserReport implements Parcelable, Serializable {
    private Long id;
    private BaseUser reporter;
    private BaseUser reported;
    private Date dateApproved;
    private String reason;

    protected UserReport(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        reporter = in.readParcelable(BaseUser.class.getClassLoader());
        reported = in.readParcelable(BaseUser.class.getClassLoader());
        reason = in.readString();
    }

    public static final Creator<UserReport> CREATOR = new Creator<UserReport>() {
        @Override
        public UserReport createFromParcel(Parcel in) {
            return new UserReport(in);
        }

        @Override
        public UserReport[] newArray(int size) {
            return new UserReport[size];
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
        dest.writeParcelable(reporter, flags);
        dest.writeParcelable(reported, flags);
        dest.writeString(reason);
    }
}
