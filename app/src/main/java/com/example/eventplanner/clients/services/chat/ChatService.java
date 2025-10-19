package com.example.eventplanner.clients.services.chat;

import com.example.eventplanner.dto.chat.ChatDto;
import com.example.eventplanner.model.chat.Chat;
import com.example.eventplanner.model.chat.ChatMessage;
import com.example.eventplanner.model.utils.PagedModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {

    @POST("chat")
    Call<Chat> add(@Body ChatDto chat);

    @GET("chat/mine")
    Call<PagedModel<Chat>> getAllMyChats(@Query("page") int page, @Query("size") int size);

    @GET("chat/mine-and/{user2Id}")
    Call<Chat> getMineUser2Id(@Path("user2Id") long user2Id);

    @PUT("chat/{id}/send-message")
    Call<Chat> sendMessage(@Path("id") long id, @Body ChatMessage message);
}
