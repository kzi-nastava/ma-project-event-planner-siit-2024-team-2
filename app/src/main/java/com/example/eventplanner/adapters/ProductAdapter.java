package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.serviceproduct.ProductDto;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<ProductDto> items;
    public OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(ProductDto product);
        void onDeleteClick(ProductDto product);
    }

    public ProductAdapter(List<ProductDto> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_product_name);
            tvPrice = view.findViewById(R.id.tv_product_price);
            tvCategory = view.findViewById(R.id.tv_product_category);
            btnEdit = view.findViewById(R.id.btn_edit_product);
            btnDelete = view.findViewById(R.id.btn_delete_product);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductDto product = items.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("%.2f €", product.getPrice()));
        holder.tvCategory.setText("Category ID: " + product.getCategoryId());

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(product);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
