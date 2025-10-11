package com.example.eventplanner.fragments.eventtype;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.model.event.EventType;

import java.util.ArrayList;
import java.util.List;

public class CreateEditEventTypeFragment extends Fragment {

    private EditText editTextName, editTextDescription;
    private Button btnSave;
    private EventTypeListViewModel viewModel;
    private Long eventTypeId = null; // null means "create", otherwise "edit"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_edit_event_type, container, false);

        editTextName = view.findViewById(R.id.editTextEventTypeName);
        editTextDescription = view.findViewById(R.id.editTextEventTypeDescription);
        btnSave = view.findViewById(R.id.btnSaveEventType);

        viewModel = new ViewModelProvider(requireActivity()).get(EventTypeListViewModel.class);

        // Check if we are editing an existing one
        if (getArguments() != null && getArguments().containsKey("id")) {
            eventTypeId = getArguments().getLong("id");
            loadEventType(eventTypeId);
        }

        btnSave.setOnClickListener(v -> saveEventType());

        return view;
    }

    private void loadEventType(Long id) {
        viewModel.getEventTypeById(id).observe(getViewLifecycleOwner(), eventType -> {
            if (eventType != null) {
                editTextName.setText(eventType.getName());
                editTextDescription.setText(eventType.getDescription());
            }
        });
    }

    private void saveEventType() {
        String name = editTextName.getText().toString().trim();
        String desc = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Name is required");
            return;
        }

        CreateEventTypeDto dto = new CreateEventTypeDto(name, desc, new ArrayList<>());

        if (eventTypeId == null) {
            // Create new
            viewModel.createEventType(dto).observe(getViewLifecycleOwner(), created -> {
                if (created != null) {
                    Toast.makeText(getContext(), "Event type created", Toast.LENGTH_SHORT).show();
                    navigateBack();
                } else {
                    Toast.makeText(getContext(), "Failed to create", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Update existing
            viewModel.updateEventType(eventTypeId, dto).observe(getViewLifecycleOwner(), updated -> {
                if (updated != null) {
                    Toast.makeText(getContext(), "Event type updated", Toast.LENGTH_SHORT).show();
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
