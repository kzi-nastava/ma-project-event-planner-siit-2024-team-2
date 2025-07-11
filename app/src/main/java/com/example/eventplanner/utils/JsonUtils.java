package com.example.eventplanner.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;

public class JsonUtils {

    /**
     * Logs exception immediately if it occurs.
     * @throws IOException If an I/O error occurs
     * @return A string containing the json content
     */
    public static String readJsonFromAssets(Context context, String filename) throws IOException {
        StringBuilder jsonString = new StringBuilder();

        try (InputStream is = context.getAssets().open(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

        } catch (IOException e) {
            Log.e("JsonUtils", "IOException thrown while reading json asset: " + e.getMessage());
            throw e;
        }

        return jsonString.toString();
    }
}