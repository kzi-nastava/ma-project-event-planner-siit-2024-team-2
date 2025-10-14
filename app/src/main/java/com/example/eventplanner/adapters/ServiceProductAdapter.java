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
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class ServiceProductAdapter extends RecyclerView.Adapter<ServiceProductAdapter.ViewHolder> {
    private List<ServiceProductSummaryDto> localDataSet = new ArrayList<>();
    private OnServiceProductClickListener listener;

    public interface OnServiceProductClickListener {
        void onMoreInfoClick(ServiceProductSummaryDto serviceProduct);

        void onHeartClick(ServiceProductSummaryDto serviceProduct, boolean isFavorite);
    }

    public ServiceProductAdapter(List<ServiceProductSummaryDto> dataSet, ServiceProductAdapter.OnServiceProductClickListener listener) {
        this.localDataSet = dataSet != null ? dataSet : new ArrayList<>();
        this.listener = listener;
    }

    public void setOnServiceProductClickListener(ServiceProductAdapter.OnServiceProductClickListener listener) {
        this.listener = listener;
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView username, email, name, price, description;
        private final Button moreInfo;
        private final ImageButton heart;

        public ViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.text_spp_username);
            email = view.findViewById(R.id.text_spp_email);
            name = view.findViewById(R.id.text_service_product_name);
            price = view.findViewById(R.id.text_service_product_price);
            description = view.findViewById(R.id.text_service_product_description);
            moreInfo = view.findViewById(R.id.btn_service_product_more_info);
            heart = view.findViewById(R.id.btn_service_product_heart);
        }
    }

    @NonNull
    @Override
    public ServiceProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_service_product_compact, viewGroup, false);
        return new ServiceProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProductAdapter.ViewHolder holder, int position) {
        ServiceProductSummaryDto dto = localDataSet.get(position);

        holder.getUsername().setText(dto.getCreatorName() != null ? dto.getCreatorName() : "Creator");
        holder.getEmail().setText(dto.getCreatorEmail() != null ? dto.getCreatorEmail() : "email@example.com");
        holder.getName().setText(dto.getName() != null ? dto.getName() : "Service");
        holder.getPrice().setText(dto.isAvailable() ? "$" + dto.getPrice() : "Not available");
        holder.getDescription().setText(dto.getDescription() != null ? dto.getDescription() : "No description");


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

    public void updateData(List<ServiceProductSummaryDto> serviceProducts) {
        this.localDataSet = serviceProducts != null ? serviceProducts : new ArrayList<>();
        notifyDataSetChanged();
    }
}