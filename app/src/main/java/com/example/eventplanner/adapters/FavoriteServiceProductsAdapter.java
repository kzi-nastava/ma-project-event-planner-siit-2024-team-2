package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.util.ArrayList;
import java.util.List;

public class FavoriteServiceProductsAdapter extends RecyclerView.Adapter<FavoriteServiceProductsAdapter.ViewHolder> {
   private List<ServiceProductSummaryDto> serviceProducts = new ArrayList<>();

   public void setServiceProducts(List<ServiceProductSummaryDto> events) {
      this.serviceProducts = events;
      notifyDataSetChanged();
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
              .inflate(android.R.layout.simple_list_item_1, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.textView.setText(serviceProducts.get(position).getName());
   }

   @Override
   public int getItemCount() {
      return serviceProducts.size();
   }

   static class ViewHolder extends RecyclerView.ViewHolder {
      TextView textView;
      ViewHolder(View itemView) {
         super(itemView);
         textView = itemView.findViewById(android.R.id.text1);
      }
   }
}
