package com.example.eventplanner.clients.interceptors;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.eventplanner.clients.utils.JwtUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class JwtInterceptor implements Interceptor {
    private final Context context;

    public JwtInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        String jwtToken = JwtUtils.getJwtToken(context);

        if (jwtToken != null) {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(chain.request());
    }
}
