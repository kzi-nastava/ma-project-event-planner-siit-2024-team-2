package com.example.eventplanner.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.dto.user.BaseUserDto;
import com.example.eventplanner.model.user.BaseUser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Parcelable, Serializable {
    private Long id;
    private String name;
    private String description;
    private EventType type;
    private BaseUser eventOrganizerDto;
    private int maxAttendances;
    private boolean open;
    private double longitude;
    private double latitude;
    private long date;
    private List<Activity> activities;
    private List<Budget> budgets;
    private List<String> invitationEmails;

    protected Event(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        description = in.readString();
        type = in.readParcelable(EventType.class.getClassLoader());
        eventOrganizerDto = in.readParcelable(BaseUser.class.getClassLoader());
        maxAttendances = in.readInt();
        open = in.readByte() != 0;
        longitude = in.readDouble();
        latitude = in.readDouble();
        date = in.readLong();
        activities = in.createTypedArrayList(Activity.CREATOR);
        budgets = in.createTypedArrayList(Budget.CREATOR);
        invitationEmails = in.createStringArrayList();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
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
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(type, flags);
        dest.writeParcelable(eventOrganizerDto, flags);
        dest.writeInt(maxAttendances);
        dest.writeByte((byte) (open ? 1 : 0));
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeLong(date);
        dest.writeTypedList(activities);
        dest.writeTypedList(budgets);
        dest.writeStringList(invitationEmails);
    }
}
