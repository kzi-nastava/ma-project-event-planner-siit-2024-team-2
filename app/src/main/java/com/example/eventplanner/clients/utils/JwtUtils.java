package com.example.eventplanner.clients.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class JwtUtils {
    public static final String PREF_NAME = "app_prefs";
    public static final String JWT_TOKEN_KEY = "jwt_token";
    private static String cachedJwtToken = null;

    private JwtUtils() {}

    public static String getJwtToken(Context context) {
        if (cachedJwtToken != null) {
            return cachedJwtToken;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(JWT_TOKEN_KEY, null);
    }

    public static void saveJwtToken(Context context, String token) {
        cachedJwtToken = token;
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JWT_TOKEN_KEY, token);
        editor.apply();
    }

    public static void clearJwtToken(Context context) {
        cachedJwtToken = null;
        if (context == null) return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JWT_TOKEN_KEY);
        editor.apply();
    }
}
