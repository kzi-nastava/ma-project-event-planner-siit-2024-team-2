package com.example.eventplanner.model.serviceproduct;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.user.ServiceProductProvider;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends ServiceProduct implements Parcelable, Serializable {
    private String specifies;
    private float duration;
    private float minEngagementDuration;
    private float maxEngagementDuration;
    private int reservationDaysDeadline;
    private int cancellationDaysDeadline;
    private boolean hasAutomaticReservation;

    protected Service(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        category = in.readParcelable(ServiceProductCategory.class.getClassLoader());
        available = in.readByte() != 0;
        visible = in.readByte() != 0;
        price = in.readDouble();
        discount = in.readDouble();
        name = in.readString();
        description = in.readString();
        images = in.createStringArrayList();
        availableEventTypes = in.createTypedArrayList(EventType.CREATOR);
        serviceProductProvider = in.readParcelable(ServiceProductProvider.class.getClassLoader());
        specifies = in.readString();
        duration = in.readFloat();
        minEngagementDuration = in.readFloat();
        maxEngagementDuration = in.readFloat();
        reservationDaysDeadline = in.readInt();
        cancellationDaysDeadline = in.readInt();
        hasAutomaticReservation = in.readByte() != 0;
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
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
        dest.writeParcelable(category, flags);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeByte((byte) (visible ? 1 : 0));
        dest.writeDouble(price);
        dest.writeDouble(discount);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(images);
        dest.writeTypedList(availableEventTypes);
        dest.writeParcelable(serviceProductProvider, flags);
        dest.writeString(specifies);
        dest.writeFloat(duration);
        dest.writeFloat(minEngagementDuration);
        dest.writeFloat(maxEngagementDuration);
        dest.writeInt(reservationDaysDeadline);
        dest.writeInt(cancellationDaysDeadline);
        dest.writeByte((byte) (hasAutomaticReservation ? 1 : 0));
    }
}
