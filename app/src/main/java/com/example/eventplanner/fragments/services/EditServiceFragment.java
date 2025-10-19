package com.example.eventplanner.fragments.services;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ImageAdapter;
import com.example.eventplanner.adapters.SelectableEventAdapter;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.*;
import java.util.stream.Collectors;

public class EditServiceFragment extends Fragment {

    private EditText inputName, inputDescription, inputPrice, inputDiscount, inputSpecifies;
    private EditText inputDuration, inputCancellationDays, inputReservationDays;
    private CheckBox cbVisible, cbAvailable, cbAutomaticReserved;
    private Spinner spinnerCategory;
    private RecyclerView rvEvents;
    private Button btnSave, btnCancel, btnAttachImages;

    private EditServiceViewModel viewModel;
    private List<EventType> selectedEvents = new ArrayList<>();
    List<Long> selectedEventIds = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private final int IMAGE_PICK_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_service, container, false);
        viewModel = new ViewModelProvider(this).get(EditServiceViewModel.class);
        initViews(v);
        observeData();

        // Check if editing
        if (getArguments() != null && getArguments().containsKey("serviceId")) {
            long serviceId = getArguments().getLong("serviceId");

            viewModel.getService(serviceId).observe(getViewLifecycleOwner(), service -> {
                if (service != null) {
                    populateForm(service);
                } else {
                    Toast.makeText(requireContext(), "Failed to load service", Toast.LENGTH_SHORT).show();
                }
            });
        }
        viewModel.getCategories();
        viewModel.getEventTypes();
        return v;
    }


    private void populateForm(Service product) {
        inputName.setText(product.getName());
        inputDescription.setText(product.getDescription());
        inputPrice.setText(String.valueOf(product.getPrice()));
        inputDiscount.setText(String.valueOf(product.getDiscount()));
        inputSpecifies.setText(product.getSpecifies());
        inputDuration.setText(String.valueOf(product.getDuration()));
        inputCancellationDays.setText(String.valueOf(product.getCancellationDaysDeadline()));
        inputReservationDays.setText(String.valueOf(product.getReservationDaysDeadline()));
        cbVisible.setChecked(product.isVisible());
        cbAvailable.setChecked(product.isAvailable());
        cbAutomaticReserved.setChecked(product.isHasAutomaticReservation());
        selectedEvents = product.getAvailableEventTypes() != null
                ? new ArrayList<EventType>(product.getAvailableEventTypes())
                : new ArrayList<>();

        images.clear();
        images.addAll(product.getImageEncodedNames());


        // Set spinner selection
        viewModel.getCategories().observe(getViewLifecycleOwner(), cats -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    cats.stream().map(c -> c.getName()).toArray(String[]::new)
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            // select the correct category
            for (int i = 0; i < cats.size(); i++) {
                if (cats.get(i).getId().equals(product.getCategory().getId())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        });
    }


    private void initViews(View v) {
        inputName = v.findViewById(R.id.nameEditText);
        inputDescription = v.findViewById(R.id.descriptionEditText);
        inputPrice = v.findViewById(R.id.priceEditText);
        inputDiscount = v.findViewById(R.id.discountEditText);
        inputSpecifies = v.findViewById(R.id.specifiesEditText);
        inputDuration = v.findViewById(R.id.durationEditText);
        inputCancellationDays = v.findViewById(R.id.cancellationDeadlineEditText);
        inputReservationDays = v.findViewById(R.id.reservationDeadlineEditText);

        cbVisible = v.findViewById(R.id.checkboxVisible);
        cbAvailable = v.findViewById(R.id.checkboxAvailable);
        cbAutomaticReserved = v.findViewById(R.id.checkboxAutomaticReserved);
        spinnerCategory = v.findViewById(R.id.spinner_service_categories);
        rvEvents = v.findViewById(R.id.rvEventsService);

        RecyclerView recyclerView = v.findViewById(R.id.rv_service_images);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(images);
        recyclerView.setAdapter(imageAdapter);

        btnSave = v.findViewById(R.id.btn_save);
        btnCancel = v.findViewById(R.id.btn_cancel);
        btnAttachImages = v.findViewById(R.id.btn_attach_photo);

        btnAttachImages.setOnClickListener(vw -> openGallery());
        btnSave.setOnClickListener(vw -> onSave());
        btnCancel.setOnClickListener(vw -> requireActivity().onBackPressed());
    }

    private void observeData() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), cats -> {
            ArrayAdapter<ServiceProductCategory> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    cats
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        });
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), eventTypes -> {
            rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));

            if (getArguments() != null && getArguments().containsKey("serviceId")) {
                long serviceId = getArguments().getLong("serviceId");

                viewModel.getService(serviceId).observe(getViewLifecycleOwner(), service -> {
                    if (service != null) {
                        populateForm(service);

                        selectedEvents = service.getAvailableEventTypes() != null
                                ? new ArrayList<>(service.getAvailableEventTypes())
                                : new ArrayList<>();

                        List<Long> selectedEventIds = selectedEvents.stream()
                                .map(EventType::getId)
                                .collect(Collectors.toList());

                        SelectableEventAdapter adapter = new SelectableEventAdapter(
                                eventTypes,
                                new HashSet<>(selectedEventIds),
                                (eventId, selected) -> {
                                    if (selected) selectedEventIds.add(eventId);
                                    else selectedEventIds.remove(eventId);
                                }
                        );

                        rvEvents.setAdapter(adapter);
                    } else {
                        Toast.makeText(requireContext(), "Failed to load service", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                selectedEvents = new ArrayList<>();
                List<Long> selectedEventIds = new ArrayList<>();

                SelectableEventAdapter adapter = new SelectableEventAdapter(
                        eventTypes,
                        new HashSet<>(), // empty selection
                        (eventId, selected) -> {
                            if (selected) selectedEventIds.add(eventId);
                            else selectedEventIds.remove(eventId);
                        }
                );

                rvEvents.setAdapter(adapter);
            }
        });


        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Service saved successfully!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void onSave() {
        String name = inputName.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        double price = Double.parseDouble(inputPrice.getText().toString());
        double discount = Double.parseDouble(inputDiscount.getText().toString());
        boolean visible = cbVisible.isChecked();
        boolean available = cbAvailable.isChecked();
        long categoryId = spinnerCategory.getSelectedItemPosition() + 1;
        long providerId = UserIdUtils.getUserId(requireContext());
        String specifics = inputSpecifies.getText().toString().trim();
        float duration = Float.parseFloat(inputDuration.getText().toString());
        int canc = Integer.parseInt(inputCancellationDays.getText().toString());
        int res = Integer.parseInt(inputReservationDays.getText().toString());
        boolean autoReserved = cbAutomaticReserved.isChecked();

        ServiceDto dto = new ServiceDto(
                categoryId,
                available,
                visible,
                price,
                discount,
                name,
                description,
                selectedEventIds,
                providerId,
                specifics,
                duration,
                canc,
                res,
                autoReserved,
                images
        );

        // If editing, include product ID
        if (getArguments() != null && getArguments().containsKey("service")) {
            ServiceDto existing = (ServiceDto) getArguments().getSerializable("service");
            dto.setId(existing.getId());
            viewModel.updateService(dto);
        } else {
            images.add("slike");
            dto.setImages(images);
            viewModel.createService(dto);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    images.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                images.add(data.getData().toString());
            }
        }
    }
}