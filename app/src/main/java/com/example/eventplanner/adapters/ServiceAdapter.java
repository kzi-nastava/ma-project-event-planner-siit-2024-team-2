package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<ServiceDto> services;
    private List<ServiceDto> filteredServices;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onEditClick(ServiceDto service);
        void onDeleteClick(ServiceDto service);
    }

    public ServiceAdapter(List<ServiceDto> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
        this.filteredServices = services;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceDto service = filteredServices.get(position);

        holder.title.setText(service.getName());
        holder.description.setText(service.getDescription());
        holder.price.setText("Price: " + service.getPrice() + "$");

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(service);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(service);
        });
    }

    @Override
    public int getItemCount() {
        return services != null ? filteredServices.size() : 0;
    }

    public void setServices(List<ServiceDto> newServices) {
        this.services = newServices;
        filter(null);
    }

    public void filter(String query) {
        filteredServices.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredServices.addAll(services);
        } else {
            String lowerQuery = query.toLowerCase();
            for (ServiceDto s : services) {
                if (s.getName().toLowerCase().contains(lowerQuery)) {
                    filteredServices.add(s);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price;
        ImageView image;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.service_title);
            description = itemView.findViewById(R.id.service_description);
            price = itemView.findViewById(R.id.service_price);
            image = itemView.findViewById(R.id.service_image);
            btnEdit = itemView.findViewById(R.id.edit_service_button);
            btnDelete = itemView.findViewById(R.id.delete_service_button);
        }
    }
}
