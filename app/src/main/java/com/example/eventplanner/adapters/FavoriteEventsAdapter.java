package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventSummaryDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteEventsAdapter extends RecyclerView.Adapter<FavoriteEventsAdapter.ViewHolder> {

    private List<EventSummaryDto> events = new ArrayList<>();

    // Callbacks for clicks
    public interface OnEventClickListener {
        void onMoreInfoClick(EventSummaryDto event);
        void onHeartClick(EventSummaryDto event);
    }

    private OnEventClickListener listener;

    public void setEvents(List<EventSummaryDto> events) {
        this.events = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteEventsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteEventsAdapter.ViewHolder holder, int position) {
        EventSummaryDto event = events.get(position);

        holder.textEventName.setText(event.getName());
        holder.textEventDate.setText(event.getDate() != 0 ? new Date(event.getDate()).toString() : "");


        // Handle clicks
        holder.btnMoreInfo.setOnClickListener(v -> {
            if (listener != null) listener.onMoreInfoClick(event);
        });

        holder.btnHeart.setOnClickListener(v -> {
            if (listener != null) listener.onHeartClick(event);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageEvent;
        TextView textEventName;
        TextView textEventDate;
        Button btnMoreInfo;
        ImageButton btnHeart;

        ViewHolder(View itemView) {
            super(itemView);
            imageEvent = itemView.findViewById(R.id.image_event_picture);
            textEventName = itemView.findViewById(R.id.text_event_name);
            textEventDate = itemView.findViewById(R.id.text_event_date);
            btnMoreInfo = itemView.findViewById(R.id.btn_event_more_info);
            btnHeart = itemView.findViewById(R.id.btn_event_heart);
        }
    }
}
