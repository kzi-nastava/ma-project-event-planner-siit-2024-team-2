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
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteServiceProductsAdapter extends RecyclerView.Adapter<FavoriteServiceProductsAdapter.ViewHolder> {

   private List<ServiceProductSummaryDto> serviceProducts = new ArrayList<>();

   // Callbacks for clicks
   public interface OnServiceProductClickListener {
      void onMoreInfoClick(ServiceProductSummaryDto event);
      void onHeartClick(ServiceProductSummaryDto event);
   }

   private OnServiceProductClickListener listener;

   public void setServiceProducts(List<ServiceProductSummaryDto> serviceProducts) {
      this.serviceProducts = serviceProducts != null ? serviceProducts : new ArrayList<>();
      notifyDataSetChanged();
   }

   public void setOnServiceProductClickListener(OnServiceProductClickListener listener) {
      this.listener = listener;
   }

   @NonNull
   @Override
   public FavoriteServiceProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(R.layout.item_favorite_event, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull FavoriteServiceProductsAdapter.ViewHolder holder, int position) {
      ServiceProductSummaryDto serviceProduct = serviceProducts.get(position);

      holder.textServiceProductName.setText(serviceProduct.getName());
      holder.textServiceProductPrice.setText(String.valueOf(serviceProduct.getPrice()));


      // Handle clicks
      holder.btnMoreInfo.setOnClickListener(v -> {
         if (listener != null) listener.onMoreInfoClick(serviceProduct);
      });

      holder.btnHeart.setOnClickListener(v -> {
         if (listener != null) listener.onHeartClick(serviceProduct);
      });
   }

   @Override
   public int getItemCount() {
      return serviceProducts.size();
   }

   static class ViewHolder extends RecyclerView.ViewHolder {
      ImageView imageServiceProduct;
      TextView textServiceProductName;
      TextView textServiceProductPrice;
      Button btnMoreInfo;
      ImageButton btnHeart;

      ViewHolder(View itemView) {
         super(itemView);
         imageServiceProduct = itemView.findViewById(R.id.image_event_picture);
         textServiceProductName = itemView.findViewById(R.id.text_event_name);
         textServiceProductPrice = itemView.findViewById(R.id.text_event_date);
         btnMoreInfo = itemView.findViewById(R.id.btn_event_more_info);
         btnHeart = itemView.findViewById(R.id.btn_event_heart);
      }
   }
}
