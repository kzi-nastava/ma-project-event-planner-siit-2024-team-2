package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventSummaryDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<EventSummaryDto> localDataSet = new ArrayList<>();
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onMoreInfoClick(EventSummaryDto event);
        void onHeartClick(EventSummaryDto event, boolean isFavorite);
    }

    public EventAdapter(List<EventSummaryDto> dataSet, OnEventClickListener listener) {
        this.localDataSet = dataSet != null ? dataSet : new ArrayList<>();
        this.listener = listener;
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eoUsername, eoEmail, eventName, eventDate, eventDescription;
        private final Button moreInfo;
        private final ImageButton heart;

        public ViewHolder(View view) {
            super(view);
            eoUsername = view.findViewById(R.id.text_eo_username);
            eoEmail = view.findViewById(R.id.text_eo_email);
            eventName = view.findViewById(R.id.text_event_name);
            eventDate = view.findViewById(R.id.text_event_date);
            eventDescription = view.findViewById(R.id.text_event_description);
            moreInfo = view.findViewById(R.id.btn_event_more_info);
            heart = view.findViewById(R.id.btn_event_heart);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_event_compact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventSummaryDto dto = localDataSet.get(position);

        holder.getEoUsername().setText(dto.getCreatorName());
        holder.getEoEmail().setText(dto.getCreatorEmail());
        holder.getEventName().setText(dto.getName());
        holder.getEventDescription().setText(dto.getDescription());
        holder.getEventDate().setText(
                new SimpleDateFormat("d MMMM, yyyy").format(new Date(dto.getDate()))
        );

        // Set initial heart state
        holder.getHeart().setImageResource(dto.isFavorite()
                ? R.drawable.heart_filled
                : R.drawable.heart_empty
        );

        // More Info button
        holder.getMoreInfo().setOnClickListener(v -> {
            if (listener != null) listener.onMoreInfoClick(dto);
        });

        // Heart button toggle
        holder.getHeart().setOnClickListener(v -> {
            boolean newState = !dto.isFavorite();
            dto.setFavorite(newState); // update DTO itself
            holder.getHeart().setImageResource(newState
                    ? R.drawable.heart_filled
                    : R.drawable.heart_empty
            );
            if (listener != null) listener.onHeartClick(dto, newState);
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(List<EventSummaryDto> events) {
        this.localDataSet = events != null ? events : new ArrayList<>();
        notifyDataSetChanged();
    }
}
