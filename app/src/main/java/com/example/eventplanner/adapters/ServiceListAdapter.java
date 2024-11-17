package com.example.eventplanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.ServiceEditActivity;
import com.example.eventplanner.model.Service;

import java.util.ArrayList;
import java.util.List;

public class ServiceListAdapter extends ArrayAdapter<Service> {
    private ArrayList<Service> aServices;

    public ServiceListAdapter(Context context, ArrayList<Service> services) {
        super(context, R.layout.service_card, services);
        aServices = services;
    }

    @Override
    public int getCount() {
        return aServices.size();
    }

    @Nullable
    @Override
    public Service getItem(int position) {
        return aServices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Service service = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.service_card,
                    parent, false);
        }
        LinearLayout serviceCard = convertView.findViewById(R.id.service_card_item);
        ImageView imageView = convertView.findViewById(R.id.service_image);
        TextView serviceTitle = convertView.findViewById(R.id.service_title);
        TextView serviceDescription = convertView.findViewById(R.id.service_description);
        Button serviceEditButton = convertView.findViewById(R.id.service_button);

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
