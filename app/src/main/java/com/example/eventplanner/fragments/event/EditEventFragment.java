package com.example.eventplanner.fragments.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SelectLocationActivity;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditEventFragment extends Fragment {

    private EditEventViewModel viewModel;

    private TextInputEditText etName, etDescription, etMaxAttendances;
    private MaterialCheckBox cbIsOpen;
    private MaterialButton btnSubmit, btnSelectDate, btnSelectLocation;
    private Spinner spinnerEventType;
    private TextView tvEventTypeLabel;

    private long selectedTypeId = -1;
    private long selectedDateMillis = -1;
    private double latitude = 0, longitude = 0;
    private long eventId;

    private final List<Long> eventTypeIds = new ArrayList<>();
    private final List<String> eventTypeNames = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong("eventId", -1);
        }
    }

    private final ActivityResultLauncher<Intent> locationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    latitude = result.getData().getDoubleExtra("latitude", 0.0);
                    longitude = result.getData().getDoubleExtra("longitude", 0.0);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        viewModel = new ViewModelProvider(this).get(EditEventViewModel.class);

        etName = view.findViewById(R.id.et_event_name);
        etDescription = view.findViewById(R.id.et_event_description);
        etMaxAttendances = view.findViewById(R.id.et_event_max_attendances);
        cbIsOpen = view.findViewById(R.id.cb_is_open);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSelectLocation = view.findViewById(R.id.btn_select_location);
        btnSelectDate = view.findViewById(R.id.btn_select_date);
        spinnerEventType = view.findViewById(R.id.spinner_event_type);
        tvEventTypeLabel = view.findViewById(R.id.tv_event_type_label);

        // Load event types first
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), types -> {
            eventTypeIds.clear();
            eventTypeNames.clear();

            for (EventType type : types) {
                eventTypeIds.add(type.getId());
                eventTypeNames.add(type.getName());
            }

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    eventTypeNames
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEventType.setAdapter(spinnerAdapter);

            // Preselect if we already have event loaded
            Event currentEvent = viewModel.getCurrentEvent();
            if (currentEvent != null) {
                int index = eventTypeIds.indexOf(currentEvent.getType().getId());
                if (index >= 0) {
                    spinnerEventType.setSelection(index);
                    selectedTypeId = currentEvent.getType().getId();
                }
            }
        });

        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                selectedTypeId = eventTypeIds.get(position);
                tvEventTypeLabel.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTypeId = -1;
            }
        });

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SelectLocationActivity.class);
            locationLauncher.launch(intent);
        });
        btnSubmit.setOnClickListener(v -> handleFormSubmit());

        viewModel.getEventById(eventId).observe(getViewLifecycleOwner(), this::prefillForm);

        viewModel.getIsEventUpdated().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Event updated!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "Failed to update event!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void prefillForm(Event event) {
        if (event == null) return;

        etName.setText(event.getName());
        etDescription.setText(event.getDescription());
        etMaxAttendances.setText(String.valueOf(event.getMaxAttendances()));
        cbIsOpen.setChecked(event.isOpen());

        selectedDateMillis = event.getDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        btnSelectDate.setText(dateFormat.format(new Date(selectedDateMillis)));

        latitude = event.getLatitude();
        longitude = event.getLongitude();

        // Preselect spinner if types already loaded
        if (!eventTypeIds.isEmpty()) {
            int index = eventTypeIds.indexOf(event.getType().getId());
            if (index >= 0) {
                spinnerEventType.setSelection(index);
                selectedTypeId = event.getType().getId();
            }
        }
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(selectedDateMillis > 0 ? selectedDateMillis : MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            btnSelectDate.setText(dateFormat.format(new Date(selection)));
        });
    }

    private void handleFormSubmit() {
        String name = getText(etName);
        String description = getText(etDescription);
        int maxAttendances = Integer.parseInt(getText(etMaxAttendances));
        boolean isOpen = cbIsOpen.isChecked();

        if (selectedTypeId <= 0) {
            tvEventTypeLabel.setError("Event type is required");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateForDto = sdf.parse(sdf.format(new Date(selectedDateMillis)));

            viewModel.updateEvent(eventId, name, description, selectedTypeId, maxAttendances,
                    isOpen, longitude, latitude, dateForDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(TextInputEditText editText) {
        return TextUtils.isEmpty(editText.getText()) ? "" : editText.getText().toString();
    }
}

