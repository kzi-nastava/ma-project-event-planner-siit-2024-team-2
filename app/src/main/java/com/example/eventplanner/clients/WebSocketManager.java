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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {
    private StompClient stompClient;
    private final String url;
    private final JwtTokenProvider jwtTokenProvider;
    private final Gson gson;

    private final Map<String, MutableLiveData<WSMessage>> channels = new HashMap<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    private boolean shouldReconnect = true;
    private static final long RECONNECT_DELAY_MS = 5000;

    public WebSocketManager(String url, JwtTokenProvider jwtTokenProvider) {
        this.url = url;
        this.jwtTokenProvider = jwtTokenProvider;
        this.gson = new Gson();
    }

    public void connect() {
        if (stompClient != null && stompClient.isConnected()) return;
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);
        List<StompHeader> headers = new ArrayList<>();
        if (jwtTokenProvider != null) {
            String token = jwtTokenProvider.getJwtToken();
            if (token != null) {
                headers.add(new StompHeader("Authorization", "Bearer " + token));
            }
        }
        stompClient.connect(headers);

        Disposable lifecycleDisposable = stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    isConnected.postValue(true);
                    resubscribeToTopics();
                    break;
                case CLOSED:
                case ERROR:
                    isConnected.postValue(false);
                    if (shouldReconnect) {
                        scheduleReconnect();
                    }
                    break;
            }
        });
        compositeDisposable.add(lifecycleDisposable);
    }

    private void scheduleReconnect() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (stompClient != null) {
                stompClient.disconnect();
                compositeDisposable.clear();
            }
            connect();
        }, RECONNECT_DELAY_MS);
    }

    public void close() {
        shouldReconnect = false;
        if (stompClient != null) {
            stompClient.disconnect();
        }
        compositeDisposable.dispose();
    }

    private void resubscribeToTopics() {
        for (String dest : channels.keySet()) {
            subscribeToDestination(dest);
        }
    }

    private void subscribeToDestination(String destination) {
        Disposable topicDisposable = stompClient.topic(destination)
                .subscribe(stompMessage -> {
                    WSMessage message = gson.fromJson(stompMessage.getPayload(), WSMessage.class);
                    MutableLiveData<WSMessage> channel = channels.get(destination);
                    if (channel != null) {
                        channel.postValue(message);
                    }
                }, throwable -> Log.e("WS", "Error on topic subscription", throwable));
        compositeDisposable.add(topicDisposable);
    }

    public LiveData<WSMessage> getChannel(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        if (!channels.containsKey(dest)) {
            channels.put(dest, new MutableLiveData<>());
            if (stompClient != null && stompClient.isConnected()) {
                subscribeToDestination(dest);
            }
        }
        return channels.get(dest);
    }

    public void subscribe(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        if (!channels.containsKey(dest)) {
            getChannel(topic, subtopic, userId);
        }
    }

    public void unsubscribe(String topic, String subtopic, String userId) {
        String dest = buildDestination(topic, subtopic, userId);
        channels.remove(dest);
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