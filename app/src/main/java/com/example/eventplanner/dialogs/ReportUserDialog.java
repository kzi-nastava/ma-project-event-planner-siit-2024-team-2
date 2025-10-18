package com.example.eventplanner.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.repositories.user.UserReportRepository;
import com.example.eventplanner.clients.utils.AuthUtils;

public class ReportUserDialog extends DialogFragment {

    public interface ReportUserDialogListener {
        void onReportSubmitted();
    }

    private String reportedEmail;
    private String reportedName;
    private ReportUserDialogListener listener;
    private UserReportRepository userReportRepository;

    public static ReportUserDialog newInstance(String email, String name) {
        ReportUserDialog dialog = new ReportUserDialog();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("name", name);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ReportUserDialogListener) {
            listener = (ReportUserDialogListener) context;
        }
        userReportRepository = new UserReportRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            reportedEmail = getArguments().getString("email");
            reportedName = getArguments().getString("name");
        }

        if (!AuthUtils.isLoggedIn(getContext())) {
            return createNotLoggedInDialog();
        }

        return createReportDialog();
    }

    private Dialog createNotLoggedInDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_report_user_not_logged_in);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button btnClose = dialog.findViewById(R.id.btnClose);
            if (btnClose != null) {
                btnClose.setOnClickListener(v -> dialog.dismiss());
            }
        });
        
        return dialog;
    }

    private Dialog createReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.dialog_report_user);
        
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            EditText etReason = dialog.findViewById(R.id.etReportReason);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            Button btnReport = dialog.findViewById(R.id.btnReport);
            TextView tvReportUser = dialog.findViewById(R.id.tvReportUser);
            if (tvReportUser != null && reportedName != null) {
                tvReportUser.setText(String.format("Report %s", reportedName));
            }

            if (btnCancel != null) {
                btnCancel.setOnClickListener(v -> dialog.dismiss());
            }

            if (btnReport != null) {
                btnReport.setOnClickListener(v -> {
                    String reason = etReason != null ? etReason.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(reason)) {
                        Toast.makeText(getContext(), "Please enter a reason", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitReport(reason);
                });
            }
        });
        
        return dialog;
    }

    private void submitReport(String reason) {
        userReportRepository.reportUser(reportedEmail, reason).observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), R.string.report_success, Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onReportSubmitted();
                    }
                    dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.report_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
