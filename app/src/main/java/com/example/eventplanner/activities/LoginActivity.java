package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.JwtUtils;
import com.example.eventplanner.clients.utils.UserEmailUtils;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.dto.auth.LoginDto;
import com.example.eventplanner.dto.auth.LoginResponseDto;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton, registerButton, continueButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        continueButton = findViewById(R.id.continueWithoutAccount);

        Intent loginIntent = getIntent();
        String invitationToken = loginIntent.getStringExtra("com.example.eventplanner.retryInvitation");

        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser(invitationToken);
            }
        });


        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }

        // All validations passed
        return true;
    }

    private void loginUser(String invitationToken) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Call<LoginResponseDto> call = ClientUtils.authService.login(new LoginDto(email, password));
        call.enqueue(new SimpleCallback<>(
                response -> {
                    if (response.body() != null) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        JwtUtils.saveJwtToken(this, response.body().getJwt());
                        UserIdUtils.saveUserId(this, response.body().getId());
                        UserRoleUtils.saveUserRole(this, response.body().getRole());
                        UserEmailUtils.saveUserEmail(this, response.body().getEmail());

                        if (invitationToken != null)
                            navigateToInvitationScreen(invitationToken);
                        else
                            navigateToHomeScreen();
                    } else
                        Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show()
        ));
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToInvitationScreen(String token) {
        Intent intent = new Intent(LoginActivity.this, AcceptInvitationActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
        finish();
    }
}