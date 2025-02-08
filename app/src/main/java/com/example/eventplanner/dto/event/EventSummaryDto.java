package com.example.eventplanner.dto.event;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.utils.ReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDto implements Parcelable {
    private long id;
    private String name;
    private String description;
    private EventTypeDto type;
    private int maxAttendances;
    private boolean isOpen;
    private double longitude;
    private double latitude;
    private long date;
    private String creatorName;
    private String creatorEmail;

    protected EventSummaryDto(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        maxAttendances = in.readInt();
        isOpen = in.readByte() != 0;
        longitude = in.readDouble();
        latitude = in.readDouble();
        date = in.readLong();
        creatorName = in.readString();
        creatorEmail = in.readString();
    }

    public static final Creator<EventSummaryDto> CREATOR = new Creator<EventSummaryDto>() {
        @Override
        public EventSummaryDto createFromParcel(Parcel in) {
            return new EventSummaryDto(in);
        }

        @Override
        public EventSummaryDto[] newArray(int size) {
            return new EventSummaryDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(maxAttendances);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeLong(date);
        dest.writeString(creatorName);
        dest.writeString(creatorEmail);
    }
}
