package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.event.EventType;

import java.util.List;

public class EventTypeAdapter extends RecyclerView.Adapter<EventTypeAdapter.ViewHolder> {
    private List<EventType> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(EventType dto);
        void onDeleteClick(EventType dto);
    }

    public EventTypeAdapter(List<EventType> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_event_type_name);
            description = view.findViewById(R.id.text_event_type_description);
            btnEdit = view.findViewById(R.id.btn_edit_event_type);
            btnDelete = view.findViewById(R.id.btn_delete_event_type);
        }
    }

    @NonNull
    @Override
    public EventTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_type, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventType dto = items.get(position);
        holder.name.setText(dto.getName());
        holder.description.setText(dto.getDescription());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(dto);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(dto);
        });
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}

