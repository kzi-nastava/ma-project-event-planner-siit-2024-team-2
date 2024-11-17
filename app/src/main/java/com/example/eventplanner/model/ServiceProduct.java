package com.example.eventplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ServiceProduct implements Parcelable {
    private Long id;
    private String title;
    private String description;
    private int image;

    public ServiceProduct(Long id, String title, String description, int image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public ServiceProduct() {}

    // Parcel constructor
    protected ServiceProduct(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        title = in.readString();
        description = in.readString();
        image = in.readInt();
    }

    public static final Creator<ServiceProduct> CREATOR = new Creator<ServiceProduct>() {
        @Override
        public ServiceProduct createFromParcel(Parcel in) {
            return new ServiceProduct(in);
        }

        @Override
        public ServiceProduct[] newArray(int size) {
            return new ServiceProduct[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(id);
        }
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeInt(image);
    }
}
