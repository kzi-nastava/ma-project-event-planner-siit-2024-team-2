package com.example.eventplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventplanner.R;
import com.example.eventplanner.model.serviceproduct.Service;

import java.util.ArrayList;

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
        ImageView imageView = convertView.findViewById(R.id.service_image);
        TextView serviceTitle = convertView.findViewById(R.id.service_title);
        TextView serviceDescription = convertView.findViewById(R.id.service_description);
        Button serviceEditButton = convertView.findViewById(R.id.save_service_button);
        Button serviceDeleteButton = convertView.findViewById(R.id.delete_service_button);

        if(service != null){
            serviceTitle.setText(service.getName());
            serviceDescription.setText(service.getDescription());

            View finalConvertView = convertView;
            editService(serviceEditButton, finalConvertView, service);
            deleteService(position, serviceDeleteButton);
        }
        return convertView;
    }

    private static void editService(Button serviceEditButton, View finalConvertView, Service service) {
        serviceEditButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(finalConvertView);
            Bundle bundle = new Bundle();
            bundle.putParcelable("selectedService", service);
            navController.navigate(R.id.nav_edit_service, bundle);
        });
    }

    private void deleteService(int position, Button serviceDeleteButton) {
        serviceDeleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        aServices.remove(position);
                        notifyDataSetChanged();  // Refresh the RecyclerView
                        Toast.makeText(v.getContext(), "Service deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}
