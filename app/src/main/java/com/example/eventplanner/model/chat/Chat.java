package com.example.eventplanner.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.utils.ChatStatus;

import java.io.Serializable;
import java.util.ArrayList;
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
public class Chat implements Parcelable, Serializable {
    private Long id;
    private String lastMessage;
    private Date lastMessageDate;
    private BaseUser user1;
    private BaseUser user2;
    private Date sentAt;
    private List<ChatMessage> messages;
    private ChatStatus status;
    private boolean user1BlockedUser2;
    private boolean user2BlockedUser1;

    protected Chat(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        lastMessage = in.readString();
        long tmpLastMessageDate = in.readLong();
        lastMessageDate = tmpLastMessageDate != -1 ? new Date(tmpLastMessageDate) : null;
        user1 = in.readParcelable(BaseUser.class.getClassLoader());
        user2 = in.readParcelable(BaseUser.class.getClassLoader());
        long tmpSentAt = in.readLong();
        sentAt = tmpSentAt != -1 ? new Date(tmpSentAt) : null;
        messages = in.createTypedArrayList(ChatMessage.CREATOR);
        user1BlockedUser2 = in.readByte() != 0;
        user2BlockedUser1 = in.readByte() != 0;
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
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
        dest.writeString(lastMessage);
        dest.writeLong(lastMessageDate != null ? lastMessageDate.getTime() : -1L);
        dest.writeParcelable(user1, flags);
        dest.writeParcelable(user2, flags);
        dest.writeLong(sentAt != null ? sentAt.getTime() : -1L);
        dest.writeTypedList(messages);
        dest.writeByte((byte) (user1BlockedUser2 ? 1 : 0));
        dest.writeByte((byte) (user2BlockedUser1 ? 1 : 0));
    }
}
