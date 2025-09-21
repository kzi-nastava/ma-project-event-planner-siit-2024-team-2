package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.auth.RegisterUserDto;
import com.example.eventplanner.model.utils.UserRole;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, addressEditText, phoneEditText;
    private ImageView profileImageView;
    private Button uploadProfilePicButton, registerButton;
    private LinearLayout eventOrganizerForm;
    private LinearLayout serviceProviderForm;
    private RadioGroup userTypeRadioGroup;
    private boolean isEventOrganizer = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registers);
        profileImageView = findViewById(R.id.profileImageView);
        uploadProfilePicButton = findViewById(R.id.uploadProfilePicButton);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.registerButton);
        eventOrganizerForm = findViewById(R.id.eventOrganizerForm);
        serviceProviderForm = findViewById(R.id.serviceProviderForm);
        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        uploadProfilePicButton.setOnClickListener(v -> {
            Toast.makeText(RegisterActivity.this, "Profile picture upload feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        registerButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createUser();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.eventOrganizerRadioButton) {
                    eventOrganizerForm.setVisibility(View.VISIBLE);
                    serviceProviderForm.setVisibility(View.GONE);
                    isEventOrganizer = true;
                } else if (checkedId == R.id.serviceProviderRadioButton) {
                    eventOrganizerForm.setVisibility(View.GONE);
                    serviceProviderForm.setVisibility(View.VISIBLE);
                    isEventOrganizer = false;
                }
            }
        });
    }
    private boolean validateInputs() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            return false;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }
        if (TextUtils.isEmpty(address)) {
            addressEditText.setError("Address is required");
            return false;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            phoneEditText.setError("Valid phone number is required");
            return false;
        }

        return true;
    }

    private void createUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        RegisterUserDto dto = RegisterUserDto.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .phoneNumber(phone)
                .userRole(isEventOrganizer ? UserRole.EVENT_ORGANIZER : UserRole.SERVICE_PRODUCT_PROVIDER)
                .build();

        Call<ResponseBody> call = ClientUtils.authService.registerUser(dto);
        call.enqueue(new SimpleCallback<>(
                response -> {
                    if (response != null) {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToSignIn();
                    } else
                        Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
        ));

        Toast.makeText(this, "User " + firstName + " " + lastName + " registered successfully!", Toast.LENGTH_SHORT).show();

        resetFields();
    }

    private void resetFields() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
        addressEditText.setText("");
        phoneEditText.setText("");
    }

    private void navigateToSignIn()  {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}