package com.example.eventplanner.model.serviceproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.utils.ReviewStatus;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductReview implements Parcelable, Serializable {
    private Long id;
    private int grade;
    private String comment;
    private ServiceProduct serviceProduct;
    private BaseUser user;
    private ReviewStatus reviewStatus;

    protected ServiceProductReview(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        grade = in.readInt();
        comment = in.readString();
        user = in.readParcelable(BaseUser.class.getClassLoader());
    }

    public static final Creator<ServiceProductReview> CREATOR = new Creator<ServiceProductReview>() {
        @Override
        public ServiceProductReview createFromParcel(Parcel in) {
            return new ServiceProductReview(in);
        }

        @Override
        public ServiceProductReview[] newArray(int size) {
            return new ServiceProductReview[size];
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
        dest.writeInt(grade);
        dest.writeString(comment);
        dest.writeParcelable(user, flags);
    }
}
