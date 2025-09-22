package com.example.eventplanner;

import android.app.Application;
import android.content.Context;

import com.example.eventplanner.clients.utils.ClientUtils;

public class EventPlannerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ClientUtils.init(this);
    }
}
