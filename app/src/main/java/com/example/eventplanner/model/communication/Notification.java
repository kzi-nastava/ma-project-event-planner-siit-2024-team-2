package com.example.eventplanner.model.communication;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.utils.SimpleCardElement;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends SimpleCardElement implements Parcelable, Serializable {
    private Long id;
    private String title;
    private String message;
    private Date sentAt;
    private boolean seen;
    private boolean dismissed;
    private BaseUser user;

    protected Notification(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        message = in.readString();
        seen = in.readByte() != 0;
        dismissed = in.readByte() != 0;
        user = in.readParcelable(BaseUser.class.getClassLoader());
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
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
        dest.writeString(title);
        dest.writeString(message);
        dest.writeByte((byte) (seen ? 1 : 0));
        dest.writeByte((byte) (dismissed ? 1 : 0));
        dest.writeParcelable(user, flags);
    }

    @Override
    public String getSubtitle() {
        return DateUtils.getRelativeTimeSpanString(sentAt.getTime()).toString();
    }

    @Override
    public String getBody() {
        return message;
    }
}
