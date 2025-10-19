package com.example.eventplanner.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonLog {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void d (String tag, Object object, String prefix) {
        Log.d(tag, prefix + gson.toJson(object));
    }
    public static void d (String tag, Object object) {
        d(tag, object, "");
    }

    public static void e (String tag, Object object, String prefix) {
        Log.e(tag, prefix + gson.toJson(object));
    }
    public static void e (String tag, Object object) {
        e(tag, object, "");
    }

    public static void e (String tag, Object object, Throwable t, String prefix) {
        Log.e(tag, gson.toJson(object), t);
    }
    public static void e (String tag, Object object, Throwable t) {
        e(tag, object, t, "");
    }

    public static void w (String tag, Object object, String prefix) {
        Log.w(tag, prefix + gson.toJson(object));
    }
    public static void w (String tag, Object object) {
        w(tag, object, "");
    }

    public static void i (String tag, Object object, String prefix) {
        Log.i(tag, prefix + gson.toJson(object));
    }
    public static void i (String tag, Object object) {
        i(tag, object, "");
    }
}
