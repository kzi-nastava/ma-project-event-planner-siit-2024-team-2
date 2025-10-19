package com.example.eventplanner.fragments.eventtype;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceProductCategoryAdapter;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.fragments.eventtype.EventTypeListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class NewEventTypeFragment extends Fragment {

    private EditText editTextName, editTextDescription;
    private Button btnSave;
    private RecyclerView recyclerViewCategories;
    private ServiceProductCategoryAdapter categoryAdapter;
    private NewEventTypeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_event_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        btnSave = view.findViewById(R.id.btnSave);

        viewModel = new ViewModelProvider(this).get(NewEventTypeViewModel.class);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new ServiceProductCategoryAdapter(new ArrayList<>());
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Load categories from ViewModel
        viewModel.getServiceProductCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter = new ServiceProductCategoryAdapter(categories);
            recyclerViewCategories.setAdapter(categoryAdapter);
        });

        btnSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                editTextName.setError("Required");
                return;
            }

            List<ServiceProductCategory> selected = categoryAdapter.getSelected();

            CreateEventTypeDto dto = new CreateEventTypeDto(
                    name,
                    description,
                    selected.stream().map(ServiceProductCategory::getId).collect(Collectors.toList())
            );

            viewModel.createEventType(dto).observe(getViewLifecycleOwner(), result -> {
                if (result != null) {
                    requireActivity().onBackPressed();
                }
            });
        });
    }

}
