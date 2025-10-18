package com.example.eventplanner.utils;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Objects;
import java.util.function.Consumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleCallback<T> implements Callback<T> {
    private final Consumer<Response<T>> onSuccess;
    private final Consumer<Pair<Response<T>, String>> onError;

    public SimpleCallback(Consumer<Response<T>> onSuccess, Consumer<Pair<Response<T>, String>> onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess.accept(response);
        } else {
            String error = parseErrorBody(response.errorBody());
            Log.e("RetrofitCall",  "Failed HTTP call: " +
                    "\n\tStatus: " + response.code() +
                    "\n\tMessage: " + response.message() +
                    "\n\tError: " + error);
            onError.accept(new Pair<>(response, error));
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.e("RetrofitCall", "Error: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
        onError.accept(null);
    }

    private String parseErrorBody(ResponseBody errorBody) {
        if (errorBody == null) {
            return "Unknown error";
        }
        try {
            String errorString = errorBody.string();
            JSONObject errorJson = new JSONObject(errorString);

            if (errorJson.has("message")) {
                return errorJson.getString("message");
            }

            return errorString;

        } catch (Exception e) {
            Log.e("RetrofitCall", "Failed to parse error body", e);
            return "Unknown error";
        }
    }
}
