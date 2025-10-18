package com.example.eventplanner.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.R;
import com.example.eventplanner.dialogs.SuspendedDialog;
import com.example.eventplanner.dto.user.SuspendedDialogData;

public class AcceptInvitationActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvMessage;
    private AcceptInvitationViewModel viewModel;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_invitation);

        initViews();
        initViewModel();
        setupObservers();
        
        // Token from invitation link
        token = getTokenFromIntent();
        if (token != null) {
            viewModel.acceptInvitation(this, token);
        } else {
            navigateToHome();
        }
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
        tvMessage = findViewById(R.id.tvMessage);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(AcceptInvitationViewModel.class);
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                tvStatus.setText(isLoading ? "Processing invitation..." : "Complete");
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(message);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getNavigateToEventId().observe(this, eventId -> {
            if (eventId != null) {
                navigateToEvent(eventId);
            }
        });

        viewModel.getNavigateToHome().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                navigateToHome();
            }
        });

        viewModel.getNavigateToLogin().observe(this, shouldNavigate -> {
            if (shouldNavigate != null && shouldNavigate) {
                navigateToLogin();
            }
        });

        viewModel.getShowSuspendedDialog().observe(this, data -> {
            if (data != null) {
                showSuspendedDialog(data);
            }
        });
    }

    private String getTokenFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getQueryParameter("token");
            }
            return intent.getStringExtra("token");
        }
        return null;
    }

    private void navigateToEvent(Long eventId) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("com.example.eventplanner.navigateToEvent", eventId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("com.example.eventplanner.retryInvitation", token);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showSuspendedDialog(SuspendedDialogData data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SuspendedDialog dialog = SuspendedDialog.newInstance(data);
        dialog.show(fragmentManager, "SuspendedDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.resetNavigation();
        }
    }
}
