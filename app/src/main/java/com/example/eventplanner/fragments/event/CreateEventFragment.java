package com.example.eventplanner.fragments.event;

import static androidx.fragment.app.FragmentKt.setFragmentResult;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.Marker;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.SelectLocationActivity;
import com.example.eventplanner.fragments.SelectLocationFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;


public class CreateEventFragment extends Fragment {

    private CreateEventViewModel viewModel;

    private TextInputEditText etName, etDescription, etTypeId, etMaxAttendances;
    private long selectedDateMillis = -1;
    private MaterialCheckBox cbIsOpen;
    private MaterialButton btnSubmit, btnSelectDate, btnSelectLocation;
    private double latitude = 0, longitude = 0;

    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int PERMISSION_CODE = 100;



    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())  // Future dates only
                        .build())
                .build();

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");

        // Handle positive selection
        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateMillis = selection;  // Store the selected date in milliseconds
            String formattedDate = datePicker.getHeaderText();  // Example: "Dec 8, 2024"
            Toast.makeText(getContext(), formattedDate, Toast.LENGTH_SHORT).show();
            btnSelectDate.setText(formattedDate);
        });
    }

    private void updateMapLocation(Location location) {
        // Dobavljanje trenutne lokacije na osnovu geografske širine i dužine
        GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        // Inicijalizacija kontrolera mape koji služi za manipulisanje mapom (zum, boje, iscrtavanja...)
        // Mapa se centrira na prethodno dobavljenu poziciju
        IMapController mapController = mapView.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(currentLocation);

        // Iscrtavanje markera na mapi korišćenjem prethodno dobavljene pozicije
        Marker marker = new Marker(mapView);
        marker.setPosition(currentLocation);
        marker.setTitle("You are here");
        mapView.getOverlays().add(marker);

        // Ažuriranje prikaza mape
        mapView.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);


        viewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);
        // Initialize views
        etName = view.findViewById(R.id.et_event_name);
        etDescription = view.findViewById(R.id.et_event_description);
        etTypeId = view.findViewById(R.id.et_event_type_id);
        etMaxAttendances = view.findViewById(R.id.et_event_max_attendances);
        cbIsOpen = view.findViewById(R.id.cb_is_open);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnSelectLocation = view.findViewById(R.id.btn_select_location);
        btnSelectDate = view.findViewById(R.id.btn_select_date);

        viewModel.getIsEventCreated().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                clearForm();
            } else {
                Toast.makeText(getContext(), "Failed to Create Event!", Toast.LENGTH_SHORT).show();
            }
        });
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSubmit.setOnClickListener(v -> {
            if (validateForm())
                handleFormSubmit();
            else
                Toast.makeText(getContext(), "Please fix the errors before submitting!", Toast.LENGTH_SHORT).show();
        });

        btnSelectLocation.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SelectLocationActivity.class);
            startActivity(intent);
        });



        return view;
    }


    private void handleFormSubmit() {
        String name = getText(etName);
        String description = getText(etDescription);
        long typeId = parseLong(getText(etTypeId));
        int maxAttendances = parseInt(getText(etMaxAttendances));
        Date date = new Date(selectedDateMillis);
        boolean isOpen = cbIsOpen.isChecked();
        Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
        viewModel.createEvent(name, description, typeId, maxAttendances, isOpen, 0,
                0, date);
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

        if (!isNumeric(getText(etTypeId))) {
            etTypeId.setError("Type ID must be a valid number!");
            isValid = false;
        } else {
            etTypeId.setError(null);
        }

        if (!isNumeric(getText(etMaxAttendances))) {
            etMaxAttendances.setError("Maximum attendances must be a number!");
            isValid = false;
        } else {
            etMaxAttendances.setError(null);
        }

        if (selectedDateMillis == -1) {
            btnSelectDate.setError("Please select a date!");
            isValid = false;
        } else {
            btnSelectDate.setError(null);
        }

        return isValid;
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private String getText(TextInputEditText editText) {
        return TextUtils.isEmpty(editText.getText()) ? "" : editText.getText().toString();
    }

    private void clearForm() {
        etName.setText("");
        etDescription.setText("");
        etTypeId.setText("");
        etMaxAttendances.setText("");
        cbIsOpen.setChecked(false);
    }

}