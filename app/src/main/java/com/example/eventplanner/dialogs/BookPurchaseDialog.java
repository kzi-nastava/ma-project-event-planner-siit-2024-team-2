package com.example.eventplanner.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.event.DateRangeDto;
import com.example.eventplanner.fragments.dialog.BookPurchaseViewModel;
import com.example.eventplanner.model.event.Budget;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.google.android.material.button.MaterialButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Setter;

public class BookPurchaseDialog extends DialogFragment {
    
    public interface BookPurchaseDialogListener {
        void onBookingPurchaseCompleted(boolean success);
    }

    private static final String ARG_SERVICE_PRODUCT = "service_product";
    
    private ServiceProduct serviceProduct;
    @Setter
    private BookPurchaseDialogListener listener;
    private BookPurchaseViewModel viewModel;
    private DateRangeDto selectedTimeWindow;
    
    // UI Components
    private Spinner eventSpinner, budgetSpinner, timeWindowSpinner;
    private DatePickerDialog datePicker;
    private MaterialButton datePickerButton;
    private TimePicker timePicker;
    private EditText durationEditText;
    private TextView priceTextView, discountTextView, totalPriceTextView, eventInfoTextView, timeInfoTextView, startTimeInfoTextView, endTimeInfoTextView;
    private Button confirmButton, cancelButton;
    private LinearLayout serviceTimeLayout;

    
    private List<Event> events = new ArrayList<>();
    private List<Budget> budgets = new ArrayList<>();
    private List<String> eventNames = new ArrayList<>();
    private List<String> budgetNames = new ArrayList<>();
    private List<DateRangeDto> timeWindows = new ArrayList<>();
    private ArrayAdapter<String> eventAdapter;
    private ArrayAdapter<String> budgetAdapter;
    private ArrayAdapter<String> timeWindowAdapter;

    public static BookPurchaseDialog newInstance(ServiceProduct serviceProduct) {
        BookPurchaseDialog dialog = new BookPurchaseDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SERVICE_PRODUCT, serviceProduct);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BookPurchaseDialogListener) {
            listener = (BookPurchaseDialogListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceProduct = (ServiceProduct) getArguments().getSerializable(ARG_SERVICE_PRODUCT);
        }
        viewModel = new ViewModelProvider(this).get(BookPurchaseViewModel.class);
        viewModel.setServiceProduct(serviceProduct);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_book_purchase, null);
        
        initViews(view);
        setupAdapters();
        setupListeners();
        setupViewModel();
        
        calculateTotalPrice();
        
        viewModel.fetchEvents();
        
        return new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle(viewModel.isProduct() ? "Product Purchase" : "Service Booking")
                .setPositiveButton("Confirm", null) // We'll handle this in onStart
                .setNegativeButton("Cancel", (dialog, which) -> {
                    if (listener != null) {
                        listener.onBookingPurchaseCompleted(false);
                    }
                })
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        androidx.appcompat.app.AlertDialog dialog = (androidx.appcompat.app.AlertDialog) getDialog();
        if (dialog != null) {
            confirmButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            confirmButton.setOnClickListener(v -> confirmBookingPurchase());
        }
    }

    private void initViews(View view) {
        eventSpinner = view.findViewById(R.id.spinner_event);
        budgetSpinner = view.findViewById(R.id.spinner_budget);
        timeWindowSpinner = view.findViewById(R.id.spinner_time_window);
        datePickerButton = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        durationEditText = view.findViewById(R.id.edit_duration);
        priceTextView = view.findViewById(R.id.text_price);
        discountTextView = view.findViewById(R.id.text_discount);
        totalPriceTextView = view.findViewById(R.id.text_total_price);
        eventInfoTextView = view.findViewById(R.id.text_event_info);
        timeInfoTextView = view.findViewById(R.id.text_time_info);
        startTimeInfoTextView = view.findViewById(R.id.text_start_time_info);
        endTimeInfoTextView = view.findViewById(R.id.text_end_time_info);
        serviceTimeLayout = view.findViewById(R.id.layout_service_time);
        
        timePicker.setIs24HourView(true);
        datePickerButton.setEnabled(false);
        
        if (viewModel.isProduct()) {
            serviceTimeLayout.setVisibility(View.GONE);
        } else {
            serviceTimeLayout.setVisibility(View.VISIBLE);
            setupServiceTimeFields();
        }
    }

     private void setupServiceTimeFields() {
         if (viewModel.hasFixedDuration() && viewModel.getFixedDuration() > 0) {
             durationEditText.setVisibility(View.VISIBLE);
             durationEditText.setText(String.valueOf(viewModel.getFixedDuration()));
             durationEditText.setEnabled(false);
             viewModel.setSelectedDuration(viewModel.getFixedDuration());
         } else {
             durationEditText.setVisibility(View.VISIBLE);
             durationEditText.setHint("Duration [" + viewModel.getMinDuration() + "h - " + viewModel.getMaxDuration() + "h]");
             durationEditText.setText("");
         }
     }

    private void setupAdapters() {
        eventNames = new ArrayList<>();
        
        eventAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, eventNames);
        eventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventSpinner.setAdapter(eventAdapter);

        budgetNames = new ArrayList<>();
        
        budgetAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, budgetNames);
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetSpinner.setAdapter(budgetAdapter);

        timeWindowAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        timeWindowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeWindowSpinner.setAdapter(timeWindowAdapter);
    }

    private void setupListeners() {
        eventSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedEventIndex(position);
                viewModel.fetchBudgetsForEvent(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setSelectedEventIndex(-1);
                datePickerButton.setEnabled(false);
            }
        });

        budgetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < budgets.size()) {
                    viewModel.setSelectedBudgetId(budgets.get(position).getId());
                    if (!viewModel.isProduct() && viewModel.getSelectedEventIndex() >= 0) {
                        Event selectedEvent = events.get(viewModel.getSelectedEventIndex());
                        viewModel.fetchAvailability(serviceProduct.getId(), selectedEvent.getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setSelectedBudgetId(-1L);
                datePickerButton.setEnabled(false);
            }
        });

        timeWindowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < timeWindows.size()) {
                    selectedTimeWindow = timeWindows.get(position);
                    timePicker.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timePicker.setEnabled(false);
            }
        });

        datePickerButton.setOnClickListener(v -> {
            datePicker = DatePickerDialog.newInstance(
                    (view, year, monthOfYear, dayOfMonth) -> {
                        viewModel.setSelectedDate(LocalDate.of(year, monthOfYear + 1, dayOfMonth));
                        datePickerButton.setText(viewModel.getSelectedDate().toString());
                        updateTimeWindows();
                    });
            updateDatePicker();
            datePicker.show(getChildFragmentManager(), "DatePickerDialog");
        });

        timePicker.setOnTimeChangedListener((view, hourOfDay, minute) -> {
            if (selectedTimeWindow != null) {
                LocalTime startTime, endTime;
                Pair<LocalTime, LocalTime> times = getStartTimeAndEndTime(selectedTimeWindow);
                startTime = times.first;
                endTime = times.second;
                if (endTime == LocalTime.MIN)
                    endTime = LocalTime.MAX;
                else
                    endTime = endTime.minusSeconds(Math.round(viewModel.getSelectedDuration() * 3600));
                if (LocalTime.of(hourOfDay, minute).isBefore(startTime) || LocalTime.of(hourOfDay, minute).isAfter(endTime)) {
                    Toast.makeText(getContext(), "Selected time is not within the time window", Toast.LENGTH_SHORT).show();
                    confirmButton.setEnabled(false);
                } else
                    confirmButton.setEnabled(true);
            }
            viewModel.setSelectedTime(LocalTime.of(hourOfDay, minute));
            updateTimeSummary();
        });

        durationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double duration = Double.parseDouble(s.toString());
                    if (duration >= viewModel.getMinDuration() && duration <= viewModel.getMaxDuration()) {
                        viewModel.setSelectedDuration(duration);
                        updateTimeWindows();
                        updateTimeSummary();
                    } else {
                        Toast.makeText(getContext(), "Duration must be between " + viewModel.getMinDuration() + " and " + viewModel.getMaxDuration() + " hours", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid duration", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupViewModel() {
        viewModel.getEvents().observe(this, eventList -> {
            if (eventList != null) {
                events.clear();
                events.addAll(eventList);

                eventNames.clear();
                for (Event event : events) {
                    eventNames.add(event.getName());
                }
                eventAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getBudgets().observe(this, budgetList -> {
            if (budgetList != null) {
                budgets.clear();
                budgets.addAll(budgetList);

                budgetNames.clear();
                for (Budget budget : budgets) {
                    budgetNames.add(budget.getName());
                }
                budgetAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getAvailableDates().observe(this, dateList -> {
            if (dateList == null || dateList.isEmpty()){
                return;
            }
            if (dateList.get(0).getEnd() < dateList.get(0).getStart())
                return;

            datePickerButton.setEnabled(true);
        });

        viewModel.getBookingSuccess().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Booking created successfully", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onBookingPurchaseCompleted(true);
                    }
                    dismiss();
                }
            }
        });

        viewModel.getPurchaseSuccess().observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Purchase created successfully", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onBookingPurchaseCompleted(true);
                    }
                    dismiss();
                }
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                confirmButton.setEnabled(true);
                confirmButton.setText("Confirm");
            }
        });
    }

    private void updateTimeWindows() {
        timeWindows = viewModel.getTimeWindowsForSelectedDate();
        List<String> timeWindowStrings = new ArrayList<>();
        
        for (DateRangeDto timeWindow : timeWindows) {
            LocalTime startTime, endTime;
            Pair<LocalTime, LocalTime> times = getStartTimeAndEndTime(timeWindow);
            startTime = times.first;
            endTime = times.second;

            String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            if ("00:00".equals(endTimeStr)) {
                endTimeStr = "24:00";
            }
            
            timeWindowStrings.add(startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + endTimeStr);
        }
        
        timeWindowAdapter.clear();
        timeWindowAdapter.addAll(timeWindowStrings);
        timeWindowAdapter.notifyDataSetChanged();
        
        // Enable/disable time window spinner based on availability
        timeWindowSpinner.setEnabled(!timeWindowStrings.isEmpty());
    }

    private void updateTimeSummary() {
        if (viewModel.getSelectedDate() != null) {
            timeInfoTextView.setText("Date: " + viewModel.getSelectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            timeInfoTextView.setVisibility(View.VISIBLE);
        } else {
            timeInfoTextView.setVisibility(View.GONE);
        }

        if (viewModel.getSelectedTime() != null) {
            startTimeInfoTextView.setText("Start time: " + viewModel.getSelectedTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            startTimeInfoTextView.setVisibility(View.VISIBLE);

            if (viewModel.getSelectedDuration() > 0) {
                LocalTime endTime = viewModel.getSelectedTime().plusHours((long) viewModel.getSelectedDuration());
                String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                if ("00:00".equals(endTimeStr)) {
                    endTimeStr = "24:00";
                }
                endTimeInfoTextView.setText("End time: " + endTimeStr);
                endTimeInfoTextView.setVisibility(View.VISIBLE);
            } else {
                endTimeInfoTextView.setVisibility(View.GONE);
            }
        } else {
            startTimeInfoTextView.setVisibility(View.GONE);
            endTimeInfoTextView.setVisibility(View.GONE);
        }
    }

     private void calculateTotalPrice() {
         if (serviceProduct != null) {
             double price = serviceProduct.getPrice();
             double discount = serviceProduct.getDiscount();
             double total = price - discount;
             viewModel.setTotalPrice(total);
             
             priceTextView.setText("Price: " + String.format("%.2f", price) + " €");
             discountTextView.setText("Discount: " + String.format("%.2f", discount) + " €");
             totalPriceTextView.setText("Total: " + String.format("%.2f", total) + " €");
         }
     }



    private void confirmBookingPurchase() {
        if (!viewModel.canConfirm()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewModel.getSelectedBudgetId() == null || viewModel.getSelectedBudgetId() == -1) {
            Toast.makeText(getContext(), "Please select a budget", Toast.LENGTH_SHORT).show();
            return;
        }

        confirmButton.setEnabled(false);
        confirmButton.setText("Processing...");

        if (viewModel.isProduct()) {
            viewModel.createPurchase(viewModel.getSelectedBudgetId());
        } else {
            if (viewModel.getSelectedDate() == null || viewModel.getSelectedTime() == null) {
                Toast.makeText(getContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                confirmButton.setEnabled(true);
                confirmButton.setText("Confirm");
                return;
            }
            viewModel.createBooking(viewModel.getSelectedBudgetId());
        }
    }

    private Pair<LocalTime, LocalTime> getStartTimeAndEndTime(DateRangeDto timeWindow) {
        LocalTime startTime, endTime;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            startTime = LocalTime.ofInstant(Instant.ofEpochMilli(timeWindow.getStart()), ZoneId.systemDefault());
            endTime = LocalTime.ofInstant(Instant.ofEpochMilli(timeWindow.getEnd()), ZoneId.systemDefault());
        } else {
            Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(timeWindow.getStart());
            startTime = LocalTime.of(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
            );

            calendar.setTimeInMillis(timeWindow.getEnd());
            endTime = LocalTime.of(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND)
            );
        }
        return Pair.create(startTime, endTime);
    }
    private void updateDatePicker() {
        if (viewModel.getAvailableDates().getValue() == null || datePicker == null)
            return;
        Calendar[] days = calculateSelectableDays();
        datePicker.setSelectableDays(days);
    }
    public Calendar[] calculateSelectableDays() {

        if (viewModel.getAvailableDates().getValue() == null)
            return null;
        List<Calendar> selectableDays = new ArrayList<>();
        for (DateRangeDto dateRange : viewModel.getAvailableDates().getValue()) {
            if (dateRange.getStart() == null || dateRange.getEnd() == null)
                continue;
            if (dateRange.getEnd() < dateRange.getStart())
                continue;
            long current = dateRange.getStart();
            while (current <= dateRange.getEnd()) {
                current += 60 * 60 * 24 * 1000;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(current);
                selectableDays.add(calendar);
            }
        }
        return selectableDays.toArray(new Calendar[0]);
    }
}
