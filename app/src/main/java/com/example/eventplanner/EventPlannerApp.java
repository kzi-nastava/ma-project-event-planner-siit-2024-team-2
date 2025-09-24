package com.example.eventplanner;

import android.app.Application;

import com.example.eventplanner.clients.WebSocketManager;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.JwtUtils;

import lombok.Getter;

public class EventPlannerApp extends Application {
    @Getter
    private static WebSocketManager webSocketManager;

    @Override
    public void onCreate() {
        super.onCreate();

        ClientUtils.init(this);
        webSocketManager = new WebSocketManager(
                "ws://" + BuildConfig.IP_ADDR + "/socket/websocket",
                () -> JwtUtils.getJwtToken(getApplicationContext())
        );
        webSocketManager.connect();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        webSocketManager.close();
    }

}
