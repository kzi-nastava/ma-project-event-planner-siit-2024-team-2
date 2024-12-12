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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.fragments.services.EditServiceFragment;
import com.example.eventplanner.fragments.services.FragmentTransition;
import com.example.eventplanner.fragments.services.ServicesListFragment;
import com.example.eventplanner.fragments.services.ServicesPageFragment;
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
        LinearLayout serviceCard = convertView.findViewById(R.id.service_card_item);
        ImageView imageView = convertView.findViewById(R.id.service_image);
        TextView serviceTitle = convertView.findViewById(R.id.service_title);
        TextView serviceDescription = convertView.findViewById(R.id.service_description);
        Button serviceEditButton = convertView.findViewById(R.id.service_button);

        if(service != null){
            //imageView.setImageResource(position);
            serviceTitle.setText(service.getName());
            serviceDescription.setText(service.getDescription());

            serviceCard.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Clicked: " + service.getName()  +
                        ", id: " + service.getId().toString(), Toast.LENGTH_SHORT).show();
            });

            serviceEditButton.setOnClickListener(v -> {
                if (getContext() instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) getContext();
                    EditServiceFragment editServiceFragment = EditServiceFragment.newInstance(service);
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    transaction.replace(R.id.all_services_page, editServiceFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Error: Unable to open fragment", Toast.LENGTH_SHORT).show();
                }
            });

        }
        return convertView;
    }
}
