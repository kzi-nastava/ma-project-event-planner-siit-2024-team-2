package com.example.eventplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.example.eventplanner.model.utils.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiSelectDialogHelper {
    /**
     * Creates a multi select dialog.
     * @param values Values with which to fill the list
     * @param dialogOpener View which opens the dialog when clicked
     * @param selected Boolean list which will be filled with selected values
     * @param title Dialog title
     * @param summary Optional TextView which will be filled with selected items after confirming
     */
    public static void createDialog(String[] values, View dialogOpener, Context context,
                                    boolean[] selected, String title, TextView summary) {
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
                summary.setText(stringBuilder.toString());
            });

            builder.setNegativeButton("Cancel", (dialogInterface, position) -> {
                dialogInterface.dismiss();
            });
            builder.setNeutralButton("Clear All", (dialogInterface, position) -> {
                for (int j = 0; j < selected.length; j++) {
                    selected[j] = false;
                    itemList.clear();
                    summary.setText("");
                }
            });
            builder.show();
        });
    }
}
