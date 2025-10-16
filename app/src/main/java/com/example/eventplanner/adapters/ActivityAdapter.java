package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.ActivityDto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final List<ActivityDto> activities;
    private final OnActivityActionListener listener;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public interface OnActivityActionListener {
        void onEdit(int position, ActivityDto activity);
        void onDelete(int position, ActivityDto activity);
    }

    public ActivityAdapter(List<ActivityDto> activities, OnActivityActionListener listener) {
        this.activities = activities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityDto activity = activities.get(position);
        holder.name.setText(activity.getName());
        //holder.timeRange.setText(sdf.format(activity.getActivityStart()) + " - " + sdf.format(activity.getActivityEnd()));
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        long startMillis = activity.getActivityStart(); // millis from midnight
        long endMillis = activity.getActivityEnd();

        holder.timeRange.setText(
                timeFormat.format(new Date(startMillis)) + " - " + timeFormat.format(new Date(endMillis))
        );

        holder.location.setText(activity.getDescription());

        holder.edit.setOnClickListener(v -> listener.onEdit(position, activity));
        holder.delete.setOnClickListener(v -> listener.onDelete(position, activity));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void removeAt(int position) {
        activities.remove(position);
        notifyItemRemoved(position);
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView name, timeRange, location;
        Button edit, delete;

        ActivityViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_activity_name);
            timeRange = itemView.findViewById(R.id.text_activity_time);
            location = itemView.findViewById(R.id.text_activity_location);
            edit = itemView.findViewById(R.id.btn_edit_activity);
            delete = itemView.findViewById(R.id.btn_delete_activity);
        }
    }
}
