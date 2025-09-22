package com.example.eventplanner.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleCallback<T> implements Callback<T> {
    private final Consumer<Response<T>> onSuccess;
    private final Consumer<Response<T>> onError;

    public SimpleCallback(Consumer<Response<T>> onSuccess, Consumer<Response<T>> onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess.accept(response);
        } else {
            Log.e("RetrofitCall",  "Failed HTTP call: " +
                    "\n\tStatus: " + response.code() +
                    "\n\tMessage: " + response.message());
            onError.accept(response);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.e("RetrofitCall", "Error: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
        onError.accept(null);
    }
}
