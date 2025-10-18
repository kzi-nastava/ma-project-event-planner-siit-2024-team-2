package com.example.eventplanner.clients.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserEmailUtils {
   public static final String PREF_NAME = "app_prefs";
   public static final String USER_EMAIL_KEY = "user_email";
   private static String cachedUserEmail = null;

   private UserEmailUtils() {}

   public static String getUserEmail(Context context) {
      if (cachedUserEmail != null) {
         return cachedUserEmail;
      }
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getString(USER_EMAIL_KEY, null);
   }

   public static void saveUserEmail(Context context, String email) {
      cachedUserEmail = email;
      if (context == null) return;
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString(USER_EMAIL_KEY, email);
      editor.apply();
   }

   public static void clearUserEmail(Context context) {
      cachedUserEmail = null;
      if (context == null) return;
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.remove(USER_EMAIL_KEY);
      editor.apply();
   }
}
