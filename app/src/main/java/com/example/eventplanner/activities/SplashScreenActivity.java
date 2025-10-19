package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.JwtUtils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        //getSupportActionBar().hide();
        int SPLASH_TIME_OUT = 3000;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (JwtUtils.getJwtToken(getApplicationContext()) != null)
                intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
            else
                intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);
    }
}