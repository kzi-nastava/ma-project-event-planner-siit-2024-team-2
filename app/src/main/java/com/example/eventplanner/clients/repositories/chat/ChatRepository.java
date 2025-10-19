package com.example.eventplanner.clients.repositories.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.chat.ChatService;
import com.example.eventplanner.clients.services.chat.ChatMessageService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.chat.ChatDto;
import com.example.eventplanner.dto.chat.ChatMessageDto;
import com.example.eventplanner.model.chat.Chat;
import com.example.eventplanner.model.chat.ChatMessage;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {

    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

    public ChatRepository() {
        this.chatService = ClientUtils.chatService;
        this.chatMessageService = ClientUtils.chatMessageService;
    }

    public LiveData<PagedModel<Chat>> getAllMyChats(int page, int size) {
        MutableLiveData<PagedModel<Chat>> liveData = new MutableLiveData<>();
        chatService.getAllMyChats(page, size).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Chat> getMineUser2Id(long user2Id) {
        MutableLiveData<Chat> liveData = new MutableLiveData<>();
        chatService.getMineUser2Id(user2Id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Chat> addChat(ChatDto chatDto) {
        MutableLiveData<Chat> liveData = new MutableLiveData<>();
        chatService.add(chatDto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ChatMessage> sendMessage(ChatMessageDto messageDto) {
        MutableLiveData<ChatMessage> liveData = new MutableLiveData<>();
        chatMessageService.sendMessage(messageDto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Chat> sendMessageToChat(long chatId, ChatMessage message) {
        MutableLiveData<Chat> liveData = new MutableLiveData<>();
        chatService.sendMessage(chatId, message).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}
