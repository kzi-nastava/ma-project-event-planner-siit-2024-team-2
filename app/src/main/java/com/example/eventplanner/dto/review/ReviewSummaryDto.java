package com.example.eventplanner.dto.review;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSummaryDto implements Parcelable {
    private Double grade;
    private String comment;
    private Date createdAt;
    private String creatorName;
    private String creatorEmail;
    private String creatorProfilePicture;

    protected ReviewSummaryDto(Parcel in) {
        if (in.readByte() == 0) {
            grade = null;
        } else {
            grade = in.readDouble();
        }
        comment = in.readString();
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        creatorName = in.readString();
        creatorEmail = in.readString();
        creatorProfilePicture = in.readString();
    }

    public static final Creator<ReviewSummaryDto> CREATOR = new Creator<ReviewSummaryDto>() {
        @Override
        public ReviewSummaryDto createFromParcel(Parcel in) {
            return new ReviewSummaryDto(in);
        }

        @Override
        public ReviewSummaryDto[] newArray(int size) {
            return new ReviewSummaryDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        if (grade == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(grade);
        }
        dest.writeString(comment);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeString(creatorName);
        dest.writeString(creatorEmail);
        dest.writeString(creatorProfilePicture);
    }
}
