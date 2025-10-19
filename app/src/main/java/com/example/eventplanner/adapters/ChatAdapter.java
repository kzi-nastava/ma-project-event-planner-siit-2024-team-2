package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ImageUtil;
import com.example.eventplanner.model.chat.Chat;
import com.example.eventplanner.model.user.BaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chats = new ArrayList<>();
    private OnChatClickListener listener;
    private long myId;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public ChatAdapter(List<Chat> chats, OnChatClickListener listener, long myId) {
        this.chats = chats != null ? chats : new ArrayList<>();
        this.listener = listener;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void updateChats(List<Chat> newChats) {
        this.chats = newChats != null ? newChats : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChatUserName;
        private ImageView profilePicture;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChatUserName = itemView.findViewById(R.id.tv_chat_user_name);
            profilePicture = itemView.findViewById(R.id.profile_picture);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onChatClick(chats.get(position));
                }
            });
        }

        public void bind(Chat chat) {
            BaseUser otherUser = chat.getUser1().getId().equals(myId) ? 
                chat.getUser2() : chat.getUser1();
            
            String userName = otherUser.getFirstName() != null && otherUser.getLastName() != null ?
                otherUser.getFirstName() + " " + otherUser.getLastName() :
                otherUser.getEmail();
            
            tvChatUserName.setText(userName);

            if (otherUser.getImageEncodedName() != null && !otherUser.getImageEncodedName().isBlank())
                Glide.with(profilePicture.getContext())
                        .load(ImageUtil.getImageUrl(otherUser.getImageEncodedName()))
                        .placeholder(R.drawable.profile_picture)
                        .into(profilePicture);
            else
                profilePicture.setImageResource(R.drawable.profile_picture);
        }
    }
}
