package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.chat.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private long myId;

    public ChatMessageAdapter(List<ChatMessage> messages, long myId) {
        this.messages = messages != null ? messages : new ArrayList<>();
        this.myId = myId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages = newMessages != null ? newMessages : new ArrayList<>();
        notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layoutMessageContainer;
        private TextView tvMessageText;
        private TextView tvMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutMessageContainer = itemView.findViewById(R.id.layout_message_container);
            tvMessageText = itemView.findViewById(R.id.tv_message_text);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
        }

        public void bind(ChatMessage message) {
            tvMessageText.setText(message.getText());
            
            if (message.getSentAt() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                tvMessageTime.setText(timeFormat.format(new Date(message.getSentAt().toEpochMilli())));
            } else {
                tvMessageTime.setText("");
            }

            boolean isMyMessage = message.getToUser() != null && 
                !message.getToUser().getId().equals(myId);
            
            if (isMyMessage) {
                layoutMessageContainer.setBackgroundResource(R.drawable.message_background);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMessageContainer.getLayoutParams();
                params.gravity = android.view.Gravity.END;
                layoutMessageContainer.setLayoutParams(params);
                tvMessageText.setTextColor(itemView.getContext().getColor(android.R.color.white));
            } else {
                layoutMessageContainer.setBackgroundResource(R.drawable.message_background_other);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutMessageContainer.getLayoutParams();
                params.gravity = android.view.Gravity.START;
                layoutMessageContainer.setLayoutParams(params);
                tvMessageText.setTextColor(itemView.getContext().getColor(android.R.color.black));
            }
        }
    }
}
