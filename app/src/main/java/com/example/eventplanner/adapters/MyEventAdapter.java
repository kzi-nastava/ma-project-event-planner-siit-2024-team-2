package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.EventSummaryDto;

import java.util.Date;
import java.util.List;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.EventViewHolder> {

   private final List<EventSummaryDto> events;
   private final OnEventActionListener listener;

   public interface OnEventActionListener {
      void onViewDetails(int position, long eventId);
      void onDelete(int position, long eventId);
      void onAgenda(int position, long eventId);
   }

   public MyEventAdapter(List<EventSummaryDto> events, OnEventActionListener listener) {
      this.events = events;
      this.listener = listener;
   }

   @NonNull
   @Override
   public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.fragment_event_compact_my, parent, false);
      return new EventViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
      EventSummaryDto event = events.get(position);
      holder.title.setText(event.getName());
      holder.date.setText(event.getDate() > 0 ? new Date(event.getDate()).toString() : "");

      holder.viewDetails.setOnClickListener(v ->
              listener.onViewDetails(position, event.getId())
      );

      holder.delete.setOnClickListener(v ->
              listener.onDelete(position, event.getId())
      );
      holder.agenda.setOnClickListener(v ->
              listener.onAgenda(position, event.getId())
      );
   }

   @Override
   public int getItemCount() {
      return events.size();
   }

   static class EventViewHolder extends RecyclerView.ViewHolder {
      TextView title, date;
      Button viewDetails, delete, agenda;

      EventViewHolder(View itemView) {
         super(itemView);
         title = itemView.findViewById(R.id.text_event_title);
         date = itemView.findViewById(R.id.text_event_date);
         viewDetails = itemView.findViewById(R.id.btn_view_details);
         delete = itemView.findViewById(R.id.btn_delete_event);
         agenda = itemView.findViewById(R.id.btn_event_agenda);
      }
   }

   // ---------- helper methods for external callers ----------
   public void setItems(List<EventSummaryDto> newItems) {
      events.clear();
      if (newItems != null) events.addAll(newItems);
      notifyDataSetChanged();
   }

   /**
    * Removes item at given adapter position and notifies RecyclerView.
    * Returns the removed item or null if position invalid.
    */
   public EventSummaryDto removeAt(int position) {
      if (position < 0 || position >= events.size()) return null;
      EventSummaryDto removed = events.remove(position);
      notifyItemRemoved(position);
      // optional: update range so indexes are consistent for animations
      notifyItemRangeChanged(position, events.size() - position);
      return removed;
   }

   /**
    * Finds first item with matching id, removes it and notifies RecyclerView.
    * Returns true if found & removed.
    */
   public boolean removeById(long eventId) {
      for (int i = 0; i < events.size(); i++) {
         if (events.get(i).getId() == eventId) {
            events.remove(i);
            notifyItemRemoved(i);
            notifyItemRangeChanged(i, events.size() - i);
            return true;
         }
      }
      return false;
   }
}

