package com.example.eventplanner.model.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.utils.SimpleCardElement;

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
public class UserReport extends SimpleCardElement implements Parcelable, Serializable {
    private Long id;
    private BaseUser reporter;
    private BaseUser reported;
    private Date approvedAt;
    private String reason;
    private Date createdAt;

    protected UserReport(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        reporter = in.readParcelable(BaseUser.class.getClassLoader());
        reported = in.readParcelable(BaseUser.class.getClassLoader());
        approvedAt = new Date(in.readLong());
        reason = in.readString();
        createdAt = new Date(in.readLong());
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
        dest.writeLong(approvedAt.getTime());
        dest.writeString(reason);
        dest.writeLong(createdAt.getTime());
    }

    @Override
    public String getTitle() {
        String reporterName = getName(reporter);
        String reportedName = getName(reported);
        return String.format("**%s** reported **%s**", reporterName, reportedName);
    }

    @Override
    public String getSubtitle() {
        return DateUtils.getRelativeTimeSpanString(createdAt.getTime()).toString();
    }

    @Override
    public String getBody() {
        return "**Reason:**\n" + (reason != null ? reason : "No reason provided");
    }

    private String getName(BaseUser user) {
        if (user != null) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();

            if (firstName != null && lastName != null) {
                return firstName + " " + lastName + " (" + email + ")";
            } else if (firstName != null) {
                return firstName + " (" + email + ")";
            } else {
                return email != null ? email : "Deleted User";
            }
        }
        return "Deleted User";
    }
}
