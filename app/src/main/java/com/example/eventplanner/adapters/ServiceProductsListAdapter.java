package com.example.eventplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eventplanner.R;
import com.example.eventplanner.model.ServiceProduct;

import java.util.ArrayList;

public class ServiceProductsListAdapter extends ArrayAdapter<ServiceProduct> {
    private ArrayList<ServiceProduct> aServiceProducts;

    public ServiceProductsListAdapter(Context context, ArrayList<ServiceProduct> services) {
        super(context, R.layout.service_product_card, services);
        aServiceProducts = services;
    }

    @Override
    public int getCount() {
        return aServiceProducts.size();
    }

    @Nullable
    @Override
    public ServiceProduct getItem(int position) {
        return aServiceProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ServiceProduct service = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_product_card,
                    parent, false);
        }
        LinearLayout serviceCard = convertView.findViewById(R.id.service_product_card_item);
        ImageView imageView = convertView.findViewById(R.id.service_product_image);
        TextView serviceTitle = convertView.findViewById(R.id.service_product_title);
        TextView serviceDescription = convertView.findViewById(R.id.service_product_description);
        Button serviceEditButton = convertView.findViewById(R.id.service_product_button);

        if(service != null){
            imageView.setImageResource(service.getImage());
            serviceTitle.setText(service.getTitle());
            serviceDescription.setText(service.getDescription());

            serviceCard.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Clicked: " + service.getTitle()  +
                        ", id: " + service.getId().toString(), Toast.LENGTH_SHORT).show();
            });

            serviceEditButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Pressed button: " + service.getTitle()  +
                        ", id: " + service.getId().toString(), Toast.LENGTH_SHORT).show();
            });
        }
        return convertView;
    }
}
