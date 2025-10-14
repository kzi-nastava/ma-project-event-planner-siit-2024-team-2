package com.example.eventplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.model.utils.UserRole;

public class RoleGuard {

    public static boolean canAccess(Context context, UserRole... allowedRoles) {
        if (UserRoleUtils.hasAnyRole(context, allowedRoles)) {
            return true;
        } else {
            Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController((Activity) context, R.id.fragment_nav_content_main);
            navController.navigate(R.id.fragment_nav_content_main);
            return false;
        }
    }
}
