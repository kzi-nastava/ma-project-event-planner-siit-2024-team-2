package com.example.eventplanner.fragments.services;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.PhotosAdapter;
import com.example.eventplanner.databinding.FragmentEditServiceBinding;
import com.example.eventplanner.model.serviceproduct.Service;

import java.util.ArrayList;
import java.util.List;


public class EditServiceFragment extends Fragment{

    private FragmentEditServiceBinding binding;
    private Service service;
    private static final int PICK_IMAGE_REQUEST = 1;

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
        TextView deleteInstruction = view.findViewById(R.id.delete_instruction_text);

        TextView servicePrice = view.findViewById(R.id.priceEditText);
        TextView serviceDiscount = view.findViewById(R.id.discountEditText);
        // Check boxes for event types (dynamically)

        RadioButton serviceRadioVisibility = view.findViewById(R.id.radio_visibility);
        RadioButton serviceRadioAvailability = view.findViewById(R.id.radio_availability);
        TextView serviceDuration = view.findViewById(R.id.durationEditText);
        TextView serviceCancellationDeadline = view.findViewById(R.id.cancellationDeadlineEditText);
        TextView serviceReservationDeadline = view.findViewById(R.id.reservationDeadlineEditText);

        Button saveBtn = view.findViewById(R.id.btn_save);
        Button cancelBtn = view.findViewById(R.id.btn_cancel);
        Button attachPhotoBtn = view.findViewById(R.id.btn_attach_photo);

        navigateBack(view, saveBtn, true);
        navigateBack(view, cancelBtn, false);
        attachPhotoBtn.setOnClickListener(view1 -> openGallery());

        if (getArguments() != null) {
            service = getArguments().getParcelable("selectedService");

            if (service != null) {
                setServiceAttributes(serviceCategory, serviceName, serviceDescription, serviceSpecifies, deleteInstruction, servicePrice, serviceDiscount,
                        serviceRadioVisibility, serviceRadioAvailability, serviceDuration, serviceReservationDeadline, serviceCancellationDeadline);
            }
        }
    }

    private void setServiceAttributes(Spinner serviceCategory, TextView serviceName, TextView serviceDescription, TextView serviceSpecifies, TextView deleteInstruction,
                                      TextView servicePrice, TextView serviceDiscount, RadioButton serviceRadioVisibility, RadioButton serviceRadioAvailability,
                                      TextView serviceDuration, TextView serviceReservationDeadline, TextView serviceCancellationDeadline) {
        setServiceCategory(serviceCategory);

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

        setServicePhotos(deleteInstruction);
    }

    private void setServiceCategory(Spinner serviceCategory) {
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
    }

    private void setServicePhotos(TextView deleteInstruction) {
        RecyclerView photosRecyclerView = binding.photosRecyclerView;
        List<Uri> photoList = new ArrayList<>();
        photoList.add(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.catering));

        if (photoList.isEmpty())
            deleteInstruction.setText("There aren't any photos of the service yet.");

        PhotosAdapter photosAdapter = new PhotosAdapter(photoList);
        photosRecyclerView.setAdapter(photosAdapter);

        // Set up RecyclerView with GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        photosRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private static void navigateBack(@NonNull View view, Button cancelBtn, boolean showSaveToast) {
        cancelBtn.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigateUp();
            if (showSaveToast)
                Toast.makeText(view.getContext(), "The service successfully saved.", Toast.LENGTH_SHORT).show();
        });
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // Filter for images only
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            List<String> images = service.getImages();
            images.add(imageUri.toString());
            service.setImages(images);
            Toast.makeText(getContext(), "The photo successfully chosen.", Toast.LENGTH_SHORT).show();
        }
    }
}