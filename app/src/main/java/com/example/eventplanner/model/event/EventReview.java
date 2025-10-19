package com.example.eventplanner.model.event;

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
public class EventReview implements Parcelable, Serializable {
    private Long id;
    private double grade;
    private String comment;
    private BaseUser user;
    private Event event;
    private ReviewStatus reviewStatus;

    protected EventReview(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        grade = in.readDouble();
        comment = in.readString();
        user = in.readParcelable(BaseUser.class.getClassLoader());
        event = in.readParcelable(Event.class.getClassLoader());
    }

    public static final Creator<EventReview> CREATOR = new Creator<EventReview>() {
        @Override
        public EventReview createFromParcel(Parcel in) {
            return new EventReview(in);
        }

        @Override
        public EventReview[] newArray(int size) {
            return new EventReview[size];
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
        dest.writeDouble(grade);
        dest.writeString(comment);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(event, flags);
    }
}
