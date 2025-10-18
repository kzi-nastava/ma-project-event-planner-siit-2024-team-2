package com.example.eventplanner.clients.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.eventplanner.model.utils.UserRole;

public class UserRoleUtils {
    public static final String PREF_NAME = "app_prefs";
    public static final String USER_ROLE_KEY = "user_role";
    private static UserRole cachedUserRole = null;

    private UserRoleUtils() {}

    public static String getUserRole(Context context) {
        if (cachedUserRole != null) {
            return cachedUserRole.toString();
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ROLE_KEY, null);
    }

    public static void saveUserRole(Context context, UserRole userRole) {
        cachedUserRole = userRole;
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ROLE_KEY, userRole.toString());
        editor.apply();
    }

    public static void clearUserRole(Context context) {
        cachedUserRole = null;
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ROLE_KEY);
        editor.apply();
    }

    public static boolean hasAnyRole(Context context, UserRole... roles) {
        UserRole userRole = UserRole.valueOf(getUserRole(context));
        if (userRole == null) return false;

        for (UserRole role : roles) {
            if (role == userRole) return true;
        }
        return false;
    }
}
