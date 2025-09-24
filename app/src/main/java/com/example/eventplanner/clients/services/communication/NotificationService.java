package com.example.eventplanner.clients.services.communication;

import com.example.eventplanner.dto.user.NotificationDto;
import com.example.eventplanner.model.user.Notification;
import com.example.eventplanner.model.utils.PagedModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationService {

    @Headers({"Content-Type:application/json"})
    @GET("notifications")
    Call<PagedModel<Notification>> getNotifications(
            @Query("page") Integer page,
            @Query("size") Integer size);

    @Headers({"Content-Type:application/json"})
    @GET("notifications/{id}")
    Call<Notification> getNotificationById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("notifications")
    Call<Notification> createNotification(@Body NotificationDto dto);

    @Headers({"Content-Type:application/json"})
    @PUT("notifications/{id}")
    Call<Notification> updateNotification(@Path("id") Long id, @Body NotificationDto dto);

    @Headers({"Content-Type:application/json"})
    @POST("notifications/dismiss")
    Call<ResponseBody> dismissNotifications(@Body List<Long> ids);

    @Headers({"Content-Type:application/json"})
    @POST("notifications/seen")
    Call<ResponseBody> seenNotifications(@Body List<Long> ids);

    @Headers({"Content-Type:application/json"})
    @DELETE("notifications/{id}")
    Call<ResponseBody> deleteNotification(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @GET("notifications/mine")
    Call<PagedModel<Notification>> getUserNotifications(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sentAt") String sentAt); // Instant

    @Headers({"Content-Type:application/json"})
    @POST("notifications/send-category-request")
    Call<ResponseBody> sendCategoryRequest(@Body NotificationDto dto);
}