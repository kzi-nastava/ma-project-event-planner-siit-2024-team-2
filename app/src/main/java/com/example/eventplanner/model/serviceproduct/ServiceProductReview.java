package com.example.eventplanner.model.serviceproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.review.ReviewStatus;
import com.example.eventplanner.model.user.BaseUser;

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
public class ServiceProductReview extends Review implements Parcelable, Serializable {
    private ServiceProduct serviceProduct;

    protected ServiceProductReview(Parcel in) {
        id = in.readLong();
        if (in.readByte() == 0) {
            grade = null;
        } else {
            grade = in.readDouble();
        }
        comment = in.readString();
        user = in.readParcelable(BaseUser.class.getClassLoader());
        hiding = in.readByte() != 0;
        hidden = in.readByte() != 0;
        serviceProduct = in.readParcelable(ServiceProduct.class.getClassLoader());
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
        dest.writeLong(id);
        if (grade == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(grade);
        }
        dest.writeString(comment);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (hiding ? 1 : 0));
        dest.writeByte((byte) (hidden ? 1 : 0));
        dest.writeParcelable(serviceProduct, flags);
    }
}
