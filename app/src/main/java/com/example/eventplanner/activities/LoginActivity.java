package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.JwtUtils;
import com.example.eventplanner.dto.auth.LoginDto;
import com.example.eventplanner.dto.auth.LoginResponseDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.utils.JsonLog;
import com.example.eventplanner.utils.JsonUtils;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser();
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        Call<LoginResponseDto> call = ClientUtils.authService.login(new LoginDto(email, password));
        call.enqueue(new SimpleCallback<>(
                response -> {
                    if (response.body() != null) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        JwtUtils.saveJwtToken(this, response.body().getJwt());

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
    }
}