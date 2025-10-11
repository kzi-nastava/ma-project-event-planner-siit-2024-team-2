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
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.adapters.SelectableEventAdapter;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.*;

public class CreateProductFragment extends Fragment {

    private EditText inputName, inputDescription, inputPrice, inputDiscount;
    private CheckBox cbVisible, cbAvailable;
    private Spinner spinnerCategory;
    private RecyclerView rvEvents;
    private Button btnSave, btnCancel, btnAttachImages;

    private CreateProductViewModel viewModel;
    private List<Long> selectedEvents = new ArrayList<>();
    private List<String> selectedImagePaths = new ArrayList<>();
    private final int IMAGE_PICK_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_product, container, false);
        viewModel = new ViewModelProvider(this).get(CreateProductViewModel.class);
        initViews(v);
        observeData();

        // Check if editing
        if (getArguments() != null && getArguments().containsKey("product")) {
            ProductDto product = (ProductDto) getArguments().getSerializable("product");
            populateForm(product);
        }

        viewModel.getCategories();
        viewModel.getEventTypes();
        return v;
    }

    private void populateForm(ProductDto product) {
        inputName.setText(product.getName());
        inputDescription.setText(product.getDescription());
        inputPrice.setText(String.valueOf(product.getPrice()));
        inputDiscount.setText(String.valueOf(product.getDiscount()));
        cbVisible.setChecked(product.isVisible());
        cbAvailable.setChecked(product.isAvailable());
        selectedEvents = product.getAvailableEventTypeIds() != null
                ? new ArrayList<>(product.getAvailableEventTypeIds())
                : new ArrayList<>();

        selectedImagePaths = product.getImages() != null
                ? new ArrayList<>(product.getImages())
                : new ArrayList<>();


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
                if (cats.get(i).getId().equals(product.getCategoryId())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        });
    }


    private void initViews(View v) {
        inputName = v.findViewById(R.id.inputName);
        inputDescription = v.findViewById(R.id.inputDescription);
        inputPrice = v.findViewById(R.id.inputPrice);
        inputDiscount = v.findViewById(R.id.inputDiscount);
        cbVisible = v.findViewById(R.id.checkboxVisible);
        cbAvailable = v.findViewById(R.id.checkboxAvailable);
        spinnerCategory = v.findViewById(R.id.spinnerCategory);
        rvEvents = v.findViewById(R.id.rvEvents);
        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
        btnAttachImages = v.findViewById(R.id.btnAttachImages);

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
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), events -> {
            rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
            SelectableEventAdapter adapter = new SelectableEventAdapter(
                    events,
                    new HashSet<>(selectedEvents),
                    (eventId, selected) -> {
                        if (selected) selectedEvents.add(eventId);
                        else selectedEvents.remove(eventId);
                    }
            );
            rvEvents.setAdapter(adapter);
        });

        viewModel.getSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Product created successfully!", Toast.LENGTH_SHORT).show();
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

        ProductDto dto = new ProductDto(
                categoryId,
                available,
                visible,
                price,
                discount,
                name,
                description,
                selectedImagePaths,
                selectedEvents,
                providerId
        );

        // If editing, include product ID
        if (getArguments() != null && getArguments().containsKey("product")) {
            ProductDto existing = (ProductDto) getArguments().getSerializable("product");
            dto.setId(existing.getId());
            viewModel.updateProduct(dto);
        } else {
            viewModel.createProduct(dto);
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
                    selectedImagePaths.add(imageUri.toString());
                }
            } else if (data.getData() != null) {
                selectedImagePaths.add(data.getData().toString());
            }
        }
    }
}

