package com.example.eventplanner.fragments.services;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentEditServiceBinding;
import com.example.eventplanner.model.serviceproduct.Service;


public class EditServiceFragment extends Fragment{

    private FragmentEditServiceBinding binding;

    public EditServiceFragment() {}

    public static EditServiceFragment newInstance() {
        EditServiceFragment fragment = new EditServiceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditServiceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner serviceCategory = view.findViewById(R.id.spinner_service_categories);

        TextView serviceName = view.findViewById(R.id.nameEditText);
        TextView serviceDescription = view.findViewById(R.id.descriptionEditText);
        TextView serviceSpecifies = view.findViewById(R.id.specifiesEditText);

        TextView servicePrice = view.findViewById(R.id.priceEditText);
        TextView serviceDiscount = view.findViewById(R.id.discountEditText);
        // Check boxes for event types (dynamically)

        RadioButton serviceRadioVisibility = view.findViewById(R.id.radio_visibility);
        RadioButton serviceRadioAvailability = view.findViewById(R.id.radio_availability);
        TextView serviceDuration = view.findViewById(R.id.durationEditText);
        TextView serviceCancellationDeadline = view.findViewById(R.id.cancellationDeadlineEditText);
        TextView serviceReservationDeadline = view.findViewById(R.id.reservationDeadlineEditText);

        if (getArguments() != null) {
            Service service = getArguments().getParcelable("selectedService");

            if (service != null) {
                int categoryPosition = -1;

                String[] serviceCategories = getContext().getResources().getStringArray(R.array.service_categories);
                String targetService = service.getCategory().getName();

                for (int i = 0; i < serviceCategories.length; i++) {
                    if (serviceCategories[i].equals(targetService)) {
                        categoryPosition = i;
                        break;
                    }
                }
                serviceCategory.setSelection(categoryPosition);

                serviceName.setText(service.getName());
                serviceDescription.setText(service.getDescription());
                serviceSpecifies.setText(service.getSpecifies());
                servicePrice.setText(String.valueOf(service.getPrice()));
                serviceDiscount.setText(String.valueOf(service.getDiscount()));

                serviceRadioVisibility.setChecked(service.isVisible());
                serviceRadioAvailability.setChecked(service.isAvailable());
                serviceDuration.setText(String.valueOf(service.getDuration()));
                serviceReservationDeadline.setText(String.valueOf(service.getReservationDaysDeadline()));
                serviceCancellationDeadline.setText(String.valueOf(service.getCancellationDaysDeadline()));

            }

            Button saveBtn = view.findViewById(R.id.btn_save);
            Button cancelBtn = view.findViewById(R.id.btn_cancel);

            saveBtn.setOnClickListener(view1 -> {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_services);
                Toast.makeText(getContext(), "The service successfully saved.", Toast.LENGTH_SHORT).show();

            });

            cancelBtn.setOnClickListener(view1 -> {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.nav_services);
            });
        }
    }
}