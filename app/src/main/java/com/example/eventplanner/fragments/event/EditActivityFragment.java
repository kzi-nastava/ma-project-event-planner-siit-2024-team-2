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

public class EditActivityFragment extends Fragment {

   private EditText inputTitle, inputDescription, inputStartTime, inputEndTime;
   private long activityStartMillis = 0;
   private long activityEndMillis = 0;
   private Button btnUpdate;

   private long eventId, activityId;
   private ActivityDto activity;

   private void showTimePicker(EditText targetEditText) {
      Calendar calendar = Calendar.getInstance();
      int hour = calendar.get(Calendar.HOUR_OF_DAY);
      int minute = calendar.get(Calendar.MINUTE);

      TimePickerDialog timePickerDialog = new TimePickerDialog(
              requireContext(),
              (view, selectedHour, selectedMinute) -> {
                 String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                 targetEditText.setText(time);

                 long millis = selectedHour * 60 * 60 * 1000L + selectedMinute * 60 * 1000L;

                 if (targetEditText.getId() == R.id.inputStartTime) {
                    activityStartMillis = millis;
                 } else {
                    activityEndMillis = millis;
                 }
              },
              hour,
              minute,
              true
      );
      timePickerDialog.show();
   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_edit_activity, container, false);

      inputTitle = view.findViewById(R.id.inputTitle);
      inputDescription = view.findViewById(R.id.inputDescription);
      inputStartTime = view.findViewById(R.id.inputStartTime);
      inputEndTime = view.findViewById(R.id.inputEndTime);
      btnUpdate = view.findViewById(R.id.btnUpdate);

      if (getArguments() != null) {
         eventId = getArguments().getLong("eventId");
         activityId = getArguments().getLong("activityId");
         activity = (ActivityDto) getArguments().getSerializable("activity"); // Make ActivityDto Parcelable

         if (activity != null) {
            inputTitle.setText(activity.getName());
            inputDescription.setText(activity.getDescription());

            // Convert millis to HH:mm
            activityStartMillis = activity.getActivityStart();
            activityEndMillis = activity.getActivityEnd();

            int startHour = (int) (activityStartMillis / (1000 * 60 * 60)) + 1;
            int startMin = (int) ((activityStartMillis / (1000 * 60)) % 60);
            inputStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", startHour, startMin));

            int endHour = (int) (activityEndMillis / (1000 * 60 * 60)) + 1;
            int endMin = (int) ((activityEndMillis / (1000 * 60)) % 60);
            inputEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", endHour, endMin));
         }
      }

      inputStartTime.setOnClickListener(v -> showTimePicker(inputStartTime));
      inputEndTime.setOnClickListener(v -> showTimePicker(inputEndTime));
      btnUpdate.setOnClickListener(v -> updateActivity());

      return view;
   }

   private void updateActivity() {
      String title = inputTitle.getText().toString().trim();
      String description = inputDescription.getText().toString().trim();

      if (TextUtils.isEmpty(title) || activityStartMillis == 0 || activityEndMillis == 0) {
         Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
         return;
      }

      ActivityDto updatedActivity = new ActivityDto(
              activityId,
              title,
              activityStartMillis,
              activityEndMillis,
              description,
              activity.getLocation()
      );

      ClientUtils.agendaService.updateActivity(eventId, activityId, updatedActivity)
              .enqueue(new Callback<ActivityDto>() {
                 @Override
                 public void onResponse(Call<ActivityDto> call, Response<ActivityDto> response) {
                    if (response.isSuccessful()) {
                       Toast.makeText(requireContext(), "Activity updated successfully", Toast.LENGTH_SHORT).show();
                       requireActivity().onBackPressed();
                    } else {
                       Toast.makeText(requireContext(), "Failed to update activity", Toast.LENGTH_SHORT).show();
                    }
                 }

                 @Override
                 public void onFailure(Call<ActivityDto> call, Throwable t) {
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                 }
              });
   }
}

