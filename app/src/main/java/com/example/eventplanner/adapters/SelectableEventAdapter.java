package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.event.EventType;

import java.util.List;
import java.util.Set;

public class SelectableEventAdapter extends RecyclerView.Adapter<SelectableEventAdapter.ViewHolder> {

    private List<EventType> items;
    private Set<Long> selectedEventIds; // store selected ids
    private OnSelectionChangedListener listener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(long eventId, boolean selected);
    }

    public SelectableEventAdapter(List<EventType> items, Set<Long> selectedEventIds, OnSelectionChangedListener listener) {
        this.items = items;
        this.selectedEventIds = selectedEventIds;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkbox;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_event_type_name);
            checkbox = view.findViewById(R.id.checkbox_select_event);
        }
    }

    @NonNull
    @Override
    public SelectableEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selectable_event, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectableEventAdapter.ViewHolder holder, int position) {
        EventType event = items.get(position);
        holder.name.setText(event.getName());
        holder.checkbox.setChecked(selectedEventIds.contains(event.getId()));

        holder.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onSelectionChanged(event.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
