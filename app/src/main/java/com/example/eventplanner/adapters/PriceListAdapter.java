package com.example.eventplanner.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.serviceproduct.PriceListDto;

import java.util.List;
import java.util.Locale;

import io.reactivex.annotations.NonNull;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.ViewHolder> {
    private List<PriceListDto> items;
    private OnItemSaveListener listener;

    public interface OnItemSaveListener {
        void onSave(PriceListDto item);
    }

    public PriceListAdapter(List<PriceListDto> items, OnItemSaveListener listener) {
        this.items = items;
        this.listener = this.listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_price_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PriceListDto item = items.get(position);

        holder.tvName.setText(item.getName());
        holder.etPrice.setText(String.valueOf(item.getPrice()));
        holder.etDiscount.setText(String.valueOf(item.getDiscount()));
        holder.updateTotal();

        TextWatcher watcher = new SimpleTextWatcher(() -> {
            double price = parseDouble(holder.etPrice.getText().toString());
            double discount = parseDouble(holder.etDiscount.getText().toString());
            item.setPrice(price);
            item.setDiscount(discount);
            holder.updateTotal();
        });

        holder.etPrice.addTextChangedListener(watcher);
        holder.etDiscount.addTextChangedListener(watcher);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTotal;
        EditText etPrice, etDiscount;


        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            etPrice = itemView.findViewById(R.id.et_price);
            etDiscount = itemView.findViewById(R.id.et_discount);
            tvTotal = itemView.findViewById(R.id.tv_total);
        }

        void updateTotal() {
            double price = parseDouble(etPrice.getText().toString());
            double discount = parseDouble(etDiscount.getText().toString());
            tvTotal.setText(String.format(Locale.getDefault(), "%.2f", (price - discount)));
        }

        private double parseDouble(String s) {
            try {
                return Double.parseDouble(s);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable callback;

        SimpleTextWatcher(Runnable callback) {
            this.callback = callback;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.run();
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
