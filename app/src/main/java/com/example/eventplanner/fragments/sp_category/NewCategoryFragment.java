package com.example.eventplanner.fragments.sp_category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductCategoryDto;
import com.example.eventplanner.fragments.eventtype.EventTypeListViewModel;

import java.util.ArrayList;

public class NewCategoryFragment extends Fragment {

    private EditText editTextName, editTextDescription;
    private Button btnSave;
    private CategoryViewModel viewModel;
    private Long categoryId = null; // null means "create", otherwise "edit"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_category, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        btnSave = view.findViewById(R.id.btnSave);

        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        // Check if we are editing an existing one
        if (getArguments() != null && getArguments().containsKey("id")) {
            categoryId = getArguments().getLong("id");
            loadCategory(categoryId);
        }

        btnSave.setOnClickListener(v -> saveCategory());

        return view;
    }

    private void loadCategory(Long id) {
        viewModel.getCategoryById(id).observe(getViewLifecycleOwner(), category -> {
            if (category != null) {
                editTextName.setText(category.getName());
                editTextDescription.setText(category.getDescription());
            }
        });
    }

    private void saveCategory() {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return;
        }

        ServiceProductCategoryDto dto = new ServiceProductCategoryDto(name, desc);

        if (categoryId == null) {
            // Create new
            viewModel.createCategory(dto).observe(getViewLifecycleOwner(), created -> {
                if (created != null) {
                    Toast.makeText(getContext(), "Category created", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    Toast.makeText(getContext(), "Failed to create", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Update existing
            viewModel.updateCategory(categoryId, dto).observe(getViewLifecycleOwner(), updated -> {
                if (updated != null) {
                    Toast.makeText(getContext(), "Category updated", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateBack() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
        navController.popBackStack();
    }
}