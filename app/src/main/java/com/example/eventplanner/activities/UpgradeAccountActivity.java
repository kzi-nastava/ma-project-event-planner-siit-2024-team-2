package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.AuthUtils;

public class UpgradeAccountActivity extends AppCompatActivity {

    private RadioGroup radioGroupUserType;
    private RadioButton radioEventOrganizer;
    private RadioButton radioServiceProvider;
    private Button btnUpgrade;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_account);

        initViews();
        setupListeners();
        
        if (!AuthUtils.canUpgradeAccount(this)) {
            Toast.makeText(this, "You cannot upgrade your account", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        radioGroupUserType = findViewById(R.id.radioGroupUserType);
        radioEventOrganizer = findViewById(R.id.radioEventOrganizer);
        radioServiceProvider = findViewById(R.id.radioServiceProvider);
        btnUpgrade = findViewById(R.id.btnUpgrade);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupListeners() {
        btnUpgrade.setOnClickListener(v -> {
            int selectedId = radioGroupUserType.getCheckedRadioButtonId();
            boolean isEventOrganizer = selectedId == R.id.radioEventOrganizer;
            
            // Navigate to registration with upgrade mode
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("upgrade_mode", true);
            intent.putExtra("user_type", isEventOrganizer ? "eventOrganizer" : "serviceProvider");
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(v -> {
            // Logout and navigate to home
            AuthUtils.logout(this);
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
