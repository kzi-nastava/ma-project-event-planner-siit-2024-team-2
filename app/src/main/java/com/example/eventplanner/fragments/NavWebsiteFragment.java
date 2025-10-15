package com.example.eventplanner.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.HomeActivity;

public class NavWebsiteFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getArguments() != null ? getArguments().getString("url") : null;
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(R.id.nav_website, true);
    }
}
