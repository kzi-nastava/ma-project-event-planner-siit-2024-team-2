package com.example.eventplanner.fragments.event;

import android.app.TimePickerDialog;
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

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.ActivityDto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActivityFragment extends Fragment {

    private EditText inputTitle, inputDescription, inputStartTime, inputEndTime;
    private long activityStartMillis = 0;
    private long activityEndMillis = 0;
    private Button btnSave;

    private long eventId;

    private void showTimePicker(EditText targetEditText) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {
                    // Format the time as HH:mm
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    targetEditText.setText(time);

                    // Convert to milliseconds since start of day
                    long millis = selectedHour * 60 * 60 * 1000 + selectedMinute * 60 * 1000;

                    if (targetEditText.getId() == R.id.inputStartTime) {
                        activityStartMillis = millis;
                    } else {
                        activityEndMillis = millis;
                    }
                },
                hour,
                minute,
                true // 24-hour format
        );
        timePickerDialog.show();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_activity, container, false);

        inputTitle = view.findViewById(R.id.inputTitle);
        inputDescription = view.findViewById(R.id.inputDescription);
        inputStartTime = view.findViewById(R.id.inputStartTime);
        inputEndTime = view.findViewById(R.id.inputEndTime);
        btnSave = view.findViewById(R.id.btnSave);

        if (getArguments() != null) {
            eventId = getArguments().getLong("eventId");
        }

        btnSave.setOnClickListener(v -> saveActivity());
        inputStartTime.setOnClickListener(v -> showTimePicker(inputStartTime));
        inputEndTime.setOnClickListener(v -> showTimePicker(inputEndTime));
        return view;
    }

    private void saveActivity() {
        String title = inputTitle.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();
        String startTime = inputStartTime.getText().toString().trim();
        String endTime = inputEndTime.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }


        ActivityDto activity = new ActivityDto(0L,
                inputTitle.getText().toString(),
                activityStartMillis,
                activityEndMillis,
                inputDescription.getText().toString(),
                "a"
        );

        ClientUtils.agendaService.addActivity(eventId, activity).enqueue(new Callback<ActivityDto>() {
            @Override
            public void onResponse(Call<ActivityDto> call, Response<ActivityDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Activity created successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                } else {
                    Toast.makeText(requireContext(), "Failed to create activity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActivityDto> call, Throwable t) {
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
