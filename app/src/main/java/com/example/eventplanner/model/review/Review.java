package com.example.eventplanner.model.review;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

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
public class Review implements Parcelable, Serializable {
    private long id;
    private Double grade;
    private String comment;
    private BaseUser user;
    private ReviewStatus reviewStatus;
    private Date createdAt;
    private boolean hiding;
    private boolean hidden;

    protected Review(Parcel in) {
        id = in.readLong();
        if (in.readByte() == 0) {
            grade = null;
        } else {
            grade = in.readDouble();
        }
        comment = in.readString();
        user = in.readParcelable(BaseUser.class.getClassLoader());
        reviewStatus = ReviewStatus.valueOf(in.readString());
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        hiding = in.readByte() != 0;
        hidden = in.readByte() != 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
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
        dest.writeString(reviewStatus.name());
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeByte((byte) (hiding ? 1 : 0));
        dest.writeByte((byte) (hidden ? 1 : 0));
    }
}
