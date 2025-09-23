package com.example.eventplanner.clients.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserIdUtils {
   public static final String PREF_NAME = "app_prefs";
   public static final String USER_ID_KEY = "user_id";
   private static long cachedUserId = -1;

   public static long getUserId(Context context) {
      if (cachedUserId < 0) {
         return cachedUserId;
      }
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      return sharedPreferences.getLong(USER_ID_KEY, -1);
   }

   public static void saveUserId(Context context, long id) {
      cachedUserId = id;
      if (context == null) return;
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putLong(USER_ID_KEY, id);
      editor.apply();
   }

   public static void clearUserId(Context context) {
      cachedUserId = -1;
      if (context == null) return;
      SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.remove(USER_ID_KEY);
      editor.apply();
   }
}
