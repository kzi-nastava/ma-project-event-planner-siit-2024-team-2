package com.example.eventplanner.fragments.event;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SelectLocationActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateEventFragment extends Fragment {

    private CreateEventViewModel viewModel;

    private TextInputEditText etName, etDescription, etMaxAttendances;
    private MaterialCheckBox cbIsOpen;
    private MaterialButton btnSubmit, btnSelectDate, btnSelectLocation;

    private Spinner spinnerEventType;
    private TextView tvEventTypeLabel;
    private final List<Long> eventTypeIds = new ArrayList<>();
    private final List<String> eventTypeNames = new ArrayList<>();
    private long selectedTypeId = -1;

    private long selectedDateMillis = -1;
    private double latitude = 0, longitude = 0;

    private final ActivityResultLauncher<Intent> locationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    latitude = result.getData().getDoubleExtra("latitude", 0.0);
                    longitude = result.getData().getDoubleExtra("longitude", 0.0);
                    Toast.makeText(getContext(), "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        viewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        etName = view.findViewById(R.id.et_event_name);
        etDescription = view.findViewById(R.id.et_event_description);
        etMaxAttendances = view.findViewById(R.id.et_event_max_attendances);

        cbIsOpen = view.findViewById(R.id.cb_is_open);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSelectLocation = view.findViewById(R.id.btn_select_location);
        btnSelectDate = view.findViewById(R.id.btn_select_date);

        spinnerEventType = view.findViewById(R.id.spinner_event_type);
        tvEventTypeLabel = view.findViewById(R.id.tv_event_type_label);

        // Observe creation result
        viewModel.getIsEventCreated().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Failed to Create Event!", Toast.LENGTH_SHORT).show();
            }
        });

        // Populate spinner (TODO: replace with real data load)
        // prepareEventTypes();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                eventTypeNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(spinnerAdapter);

        spinnerEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedView, int position, long id) {
                selectedTypeId = eventTypeIds.get(position);
                // clear error when user chooses something
                tvEventTypeLabel.setError(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTypeId = -1;
            }
        });

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) handleFormSubmit();
            else Toast.makeText(getContext(), "Please fix the errors before submitting!", Toast.LENGTH_SHORT).show();
        });

        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SelectLocationActivity.class);
            locationLauncher.launch(intent);
        });

        return view;
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;
            String formattedDate = datePicker.getHeaderText();
            Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
            btnSelectDate.setText(formattedDate);
        });
    }

    private void handleFormSubmit() {
        String name = getText(etName);
        String description = getText(etDescription);
        int maxAttendances = Integer.parseInt(getText(etMaxAttendances));
        Date date = new Date(selectedDateMillis);
        boolean isOpen = cbIsOpen.isChecked();

        // selectedTypeId is set by spinner
        viewModel.createEvent(name, description, selectedTypeId, maxAttendances, isOpen, longitude, latitude, date);
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (getText(etName).isEmpty()) {
            etName.setError("Event name is required!");
            isValid = false;
        } else {
            etName.setError(null);
        }

        if (getText(etDescription).isEmpty()) {
            etDescription.setError("Event description is required!");
            isValid = false;
        } else {
            etDescription.setError(null);
        }

        if (getText(etMaxAttendances).isEmpty()) {
            etMaxAttendances.setError("Maximum attendances is required!");
            isValid = false;
        } else {
            try {
                Integer.parseInt(getText(etMaxAttendances));
                etMaxAttendances.setError(null);
            } catch (NumberFormatException ex) {
                etMaxAttendances.setError("Maximum attendances must be a number!");
                isValid = false;
            }
        }

        if (selectedTypeId == -1) {
            tvEventTypeLabel.setError("Please select an event type!");
            isValid = false;
        } else {
            tvEventTypeLabel.setError(null);
        }

        if (selectedDateMillis == -1) {
            btnSelectDate.setError("Please select a date!");
            isValid = false;
        } else {
            btnSelectDate.setError(null);
        }

        return isValid;
    }

    private String getText(TextInputEditText editText) {
        return TextUtils.isEmpty(editText.getText()) ? "" : editText.getText().toString();
    }

    private void clearForm() {
        etName.setText("");
        etDescription.setText("");
        etMaxAttendances.setText("");
        cbIsOpen.setChecked(false);

        // reset spinner to prompt
        spinnerEventType.setSelection(0);
        tvEventTypeLabel.setError(null);

        // reset date
        selectedDateMillis = -1;
        btnSelectDate.setText("Select Date");
    }
}
