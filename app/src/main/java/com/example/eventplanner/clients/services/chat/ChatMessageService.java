package com.example.eventplanner.clients.services.chat;

import com.example.eventplanner.dto.chat.ChatMessageDto;
import com.example.eventplanner.model.chat.ChatMessage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatMessageService {

    @POST("chat-messages/send")
    Call<ChatMessage> sendMessage(@Body ChatMessageDto message);

    @POST("chat-messages")
    Call<ChatMessage> add(@Body ChatMessageDto message);
}
