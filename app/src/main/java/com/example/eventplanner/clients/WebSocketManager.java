package com.example.eventplanner.clients;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.utils.JwtTokenProvider;
import com.example.eventplanner.dto.WSMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
public class WebSocketManager extends WebSocketListener {
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final String url;
    private final JwtTokenProvider jwtTokenProvider;
    private final Gson gson;

    private final Map<String, MutableLiveData<WSMessage>> channels = new HashMap<>();
    private final Set<String> subscriptions = new HashSet<>();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    private boolean shouldReconnect = true;
    private static final long RECONNECT_DELAY_MS = 5000;

    public WebSocketManager(String url, JwtTokenProvider jwtTokenProvider) {
        this.url = url;
        this.jwtTokenProvider = jwtTokenProvider;
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        this.gson = new Gson();
    }

    public void connect() {
        Request.Builder reqBuilder = new Request.Builder().url(url);
        if (jwtTokenProvider != null ) {
            String token = jwtTokenProvider.getJwtToken();
            if (token != null)
                reqBuilder.addHeader("Authorization", "Bearer " + token);
        }
        webSocket = client.newWebSocket(reqBuilder.build(), this);
    }

    private void scheduleReconnect() {
        new Handler(Looper.getMainLooper()).postDelayed(this::connect, RECONNECT_DELAY_MS);
    }

    public void close() {
        shouldReconnect = false;
        if (webSocket != null) webSocket.close(1000, "Closing normally");
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        isConnected.postValue(false);
        if (shouldReconnect) scheduleReconnect();
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        isConnected.postValue(false);
        if (shouldReconnect) scheduleReconnect();
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        isConnected.postValue(true);
        for (String dest : subscriptions) {
            sendSubscribe(dest);
        }
    }


    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        WSMessage message = gson.fromJson(text, WSMessage.class);
        String destination = buildDestination(message.getTopic(), message.getSubtopic(), message.getToId());
        MutableLiveData<WSMessage> channel = channels.get(destination);
        if (channel != null)
            channel.postValue(message);
    }

    public LiveData<WSMessage> getChannel(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        if (!channels.containsKey(dest)) {
            channels.put(dest, new MutableLiveData<>());
        }
        return channels.get(dest);
    }

    public void subscribe(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        subscriptions.add(dest);
        sendSubscribe(dest);
    }

    public void unsubscribe(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        subscriptions.remove(dest);
        channels.remove(dest);
        sendUnsubscribe(dest);
    }

    private void sendSubscribe(String dest) {
        if (webSocket != null) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("action", "SUBSCRIBE");
                payload.put("destination", dest);
                webSocket.send(payload.toString());
            } catch (JSONException e) {
                Log.e("WS", "Failed to subscribe", e);
            }
        }
    }

    private void sendUnsubscribe(String dest) {
        if (webSocket != null) {
            JSONObject payload = new JSONObject();
            try {
                payload.put("action", "UNSUBSCRIBE");
                payload.put("destination", dest);
                webSocket.send(payload.toString());
            } catch (JSONException e) {
                Log.e("WS", "Failed to subscribe", e);
            }
        }
    }

    private String buildDestination(String topic, String subtopic, String userId) {
        StringBuilder dest = new StringBuilder("/socket-publisher");
        if (topic != null && !topic.isEmpty()) {
            dest.append("/").append(topic);
            if (subtopic != null && !subtopic.isEmpty())
                dest.append("/").append(subtopic);
        }
        if (userId != null && !userId.isEmpty())
            dest.append("/").append(userId);

        return dest.toString();
    }
}

