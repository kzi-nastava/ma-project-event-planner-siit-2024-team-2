package com.example.eventplanner.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.user.SuspendedDialogData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SuspendedDialog extends DialogFragment {

    private SuspendedDialogData data;

    public static SuspendedDialog newInstance(SuspendedDialogData data) {
        SuspendedDialog dialog = new SuspendedDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", data);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = (SuspendedDialogData) getArguments().getSerializable("data");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_suspended);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            TextView tvSuspensionDuration = dialog.findViewById(R.id.tvSuspensionDuration);
            Button btnClose = dialog.findViewById(R.id.btnClose);

            if (tvSuspensionDuration != null && data != null) {
                String suspensionDuration = calculateSuspensionDuration(data.getSuspendedAt());
                tvSuspensionDuration.setText(getString(R.string.suspension_duration, suspensionDuration));
            }

            if (btnClose != null) {
                btnClose.setOnClickListener(v -> dialog.dismiss());
            }
        });
        
        return dialog;
    }

    private String calculateSuspensionDuration(Date suspendedAt) {
        if (suspendedAt == null) {
            return "soon";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(suspendedAt);
        calendar.add(Calendar.DAY_OF_MONTH, 3); // 3 days
        Date suspensionEnd = calendar.getTime();

        Date now = new Date();
        long diffInMillis = suspensionEnd.getTime() - now.getTime();

        if (diffInMillis <= 0) {
            return "soon";
        }

        long days = diffInMillis / (24 * 60 * 60 * 1000);
        long hours = (diffInMillis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (diffInMillis % (60 * 60 * 1000)) / (60 * 1000);

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "");
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
    }
}
