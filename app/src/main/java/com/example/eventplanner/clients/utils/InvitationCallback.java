package com.example.eventplanner.clients.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventplanner.dto.event.InvitationErrorDto;
import com.example.eventplanner.utils.JsonLog;
import com.google.gson.Gson;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvitationCallback<T> implements Callback<T> {
    private final Consumer<T> onSuccess;
    private final Consumer<InvitationErrorDto> onError;

    public InvitationCallback(Consumer<T> onSuccess, Consumer<InvitationErrorDto> onError) {
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess.accept(response.body());
        } else {
            InvitationErrorDto errorDto = parseErrorResponse(response);
            onError.accept(errorDto);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        Log.e("InvitationCallback", "Error: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
        onError.accept(null);
    }

    private InvitationErrorDto parseErrorResponse(Response<T> response) {
        if (response.errorBody() == null) {
            return null;
        }
        
        try {
            String errorBody = response.errorBody().string();
            Gson gson = new Gson();
            return gson.fromJson(errorBody, InvitationErrorDto.class);
        } catch (Exception e) {
            Log.e("InvitationCallback", "Failed to parse error response", e);
            return null;
        }
    }
}
