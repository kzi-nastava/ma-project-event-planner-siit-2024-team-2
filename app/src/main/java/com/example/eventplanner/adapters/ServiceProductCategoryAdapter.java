package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.ArrayList;
import java.util.List;

public class ServiceProductCategoryAdapter extends RecyclerView.Adapter<ServiceProductCategoryAdapter.ViewHolder> {

    private final List<ServiceProductCategory> categories = new ArrayList<>();
    private final List<ServiceProductCategory> selected = new ArrayList<>();

    public ServiceProductCategoryAdapter(List<ServiceProductCategory> categories) {
        if (categories != null) this.categories.addAll(categories);
    }

    /** Replace the list and refresh UI */
    public void setCategories(List<ServiceProductCategory> newCategories) {
        categories.clear();
        if (newCategories != null) categories.addAll(newCategories);
        selected.clear();
        notifyDataSetChanged();
    }

    /** Return a copy to avoid external modification */
    public List<ServiceProductCategory> getSelected() {
        return new ArrayList<>(selected);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs must exist in item_service_product_category.xml
            name = itemView.findViewById(R.id.textViewCategoryName);
            checkBox = itemView.findViewById(R.id.checkBoxCategory);
        }
    }

    @NonNull
    @Override
    public ServiceProductCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_product_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProductCategoryAdapter.ViewHolder holder, int position) {
        ServiceProductCategory category = categories.get(position);
        holder.name.setText(category.getName());

        // prevent recycled check change listeners from firing
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selected.contains(category));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selected.contains(category)) selected.add(category);
            } else {
                selected.remove(category);
            }
        });

        // toggle checkbox when clicking the row
        holder.itemView.setOnClickListener(v -> holder.checkBox.toggle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
