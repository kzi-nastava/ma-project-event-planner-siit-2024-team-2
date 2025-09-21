package com.example.eventplanner.clients.interceptors;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.eventplanner.activities.LoginActivity;
import com.example.eventplanner.clients.utils.JwtUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class UnauthorizedInterceptor implements Interceptor{
    private final Context context;

    public UnauthorizedInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401) {
            JwtUtils.saveJwtToken(context, null);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }

        return response;
    }
}
