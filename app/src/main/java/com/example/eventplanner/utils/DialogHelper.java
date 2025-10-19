package com.example.eventplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DialogHelper {
    /**
     * Creates a multi select dialog.
     * @param values Values with which to fill the list
     * @param dialogOpener View which opens the dialog when clicked
     * @param selected Boolean list which will be filled with selected values
     * @param title Dialog title
     * @param summary Optional TextView which will be filled with selected items after confirming
     * @param clearButton Optional View for clearing summary and selected
     */
    public static void createMultiSelectDialog(String[] values, View dialogOpener, Context context,
                                               boolean[] selected, String title, TextView summary,
                                               View clearButton) {
        List<Integer> itemList = new ArrayList<>();

        dialogOpener.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setCancelable(false);

            builder.setMultiChoiceItems(values, selected, (dialogInterface, position, checked) -> {
                if (checked) {
                    itemList.add(position);
                    Collections.sort(itemList);
                } else {
                    itemList.remove(Integer.valueOf(position));
                }
            });

            builder.setPositiveButton("OK", (dialogInterface, position) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < itemList.size(); j++) {
                    stringBuilder.append(values[itemList.get(j)]);
                    if (j != itemList.size() - 1) {
                        stringBuilder.append(", ");
                    }
                }
                if (summary != null)
                    summary.setText(stringBuilder.toString());
                if (clearButton != null)
                    clearButton.setVisibility(View.VISIBLE);
            });

            builder.setNegativeButton("Cancel", (dialogInterface, position) -> {
                dialogInterface.dismiss();
            });
            builder.setNeutralButton("Clear All", (dialogInterface, position) -> {
                Arrays.fill(selected, false);
                itemList.clear();
                if (summary != null)
                    summary.setText("");
                if (clearButton != null)
                    clearButton.setVisibility(View.GONE);
            });
            builder.show();
        });

        if (clearButton != null)
            clearButton.setOnClickListener(v ->
            {
                Arrays.fill(selected, false);
                itemList.clear();
                if (summary != null)
                    summary.setText("");
                clearButton.setVisibility(View.GONE);
            });
    }
    /**
     * Creates a date range dialog.
     * @param dialogOpener View which opens the dialog when clicked
     * @param title Dialog title
     * @param summary Optional TextView which will be filled with selected items after confirming
     * @param dateRange List which will be filled with selected date
     * @param clearButton Optional View for clearing summary and selected
     */
    public static void createDateRangeDialog(View dialogOpener, String title, TextView summary,
                                             List<Long> dateRange, FragmentManager fragmentManager,
                                             View clearButton) {
        dialogOpener.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText(title);
            MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                dateRange.clear();
                dateRange.add(selection.first);
                dateRange.add(selection.second);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String startDateString = sdf.format(new Date(selection.first));
                String endDateString = sdf.format(new Date(selection.second));

                String selectedDateRange = startDateString + " - " + endDateString;

                if (summary != null)
                    summary.setText(selectedDateRange);
                if (clearButton != null)
                    clearButton.setVisibility(View.VISIBLE);
            });
            datePicker.show(fragmentManager, "DATE_PICKER");
        });

        if (clearButton != null)
            clearButton.setOnClickListener(v ->
            {
                dateRange.clear();
                if (summary != null)
                    summary.setText("");
                clearButton.setVisibility(View.GONE);
            });
    }
}
