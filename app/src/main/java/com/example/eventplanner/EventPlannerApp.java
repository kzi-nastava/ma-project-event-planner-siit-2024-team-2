package com.example.eventplanner;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.eventplanner.clients.WebSocketManager;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.JwtUtils;

import lombok.Getter;

public class EventPlannerApp extends Application {
    public static final String CHANNEL_ID = "notifications";
    @Getter
    private static WebSocketManager webSocketManager;

    @Override
    public void onCreate() {
        super.onCreate();

        ClientUtils.init(this);
        webSocketManager = new WebSocketManager(
                "ws://" + BuildConfig.IP_ADDR + ":8080/socket/websocket",
                () -> JwtUtils.getJwtToken(getApplicationContext())
        );
        webSocketManager.connect();
        createNotificationChannel();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        webSocketManager.close();
    }

    // Function taken from the Android documentation
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
