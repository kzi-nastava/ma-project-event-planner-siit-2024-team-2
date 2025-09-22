package com.example.eventplanner.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<EventSummaryDto> localDataSet = new ArrayList<>();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eoUsername;
        private final TextView eoEmail;
        private final TextView eventName;
        private final TextView eventDate;
        private final TextView eventDescription;
        private final Button moreInfo;
        private final ImageButton heart;
        private boolean favorite;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            eoUsername = (TextView) view.findViewById(R.id.text_eo_username);
            eoEmail = (TextView) view.findViewById(R.id.text_eo_email);
            eventName = (TextView) view.findViewById(R.id.text_event_name);
            eventDate = (TextView) view.findViewById(R.id.text_event_date);
            eventDescription = (TextView) view.findViewById(R.id.text_event_description);
            moreInfo = (Button) view.findViewById(R.id.btn_event_more_info);
            heart = (ImageButton) view.findViewById(R.id.btn_event_heart);

            heart.setOnClickListener(v -> {
                favorite = !favorite;
                v.setBackgroundResource(favorite ? R.drawable.heart_filled : R.drawable.heart_empty);
            });
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public EventAdapter(List<EventSummaryDto> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_event_compact, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        EventSummaryDto dto = localDataSet.get(position);
        viewHolder.getEoUsername().setText(dto.getCreatorName());
        viewHolder.getEoEmail().setText(dto.getCreatorEmail());
        viewHolder.getEventName().setText(dto.getName());
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        viewHolder.getEventDate().setText(
                dateFormat.format(new Date(dto.getDate())));
//                LocalDateTime.ofInstant(Instant.ofEpochMilli(localDataSet.get(position).getDate()),
//                                        ZoneId.systemDefault()).toString());
        viewHolder.getEventDescription().setText(dto.getDescription());
        viewHolder.getHeart().setBackgroundResource(viewHolder.isFavorite() ? R.drawable.heart_filled : R.drawable.heart_empty);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}