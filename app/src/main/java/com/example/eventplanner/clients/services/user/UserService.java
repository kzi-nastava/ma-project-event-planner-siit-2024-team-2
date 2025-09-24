package com.example.eventplanner.clients.services.user;

import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {

 @POST("api/users/{email}/suspend")
 Call<Void> suspendUser(@Path("email") String email);

 @GET("api/users/{userId}/favorite-events")
 Call<List<EventSummaryDto>> getFavoriteEvents(@Path("userId") long userId);

 @POST("api/users/{userId}/favorite-events/{eventId}")
 Call<Void> addFavoriteEvent(@Path("userId") long userId, @Path("eventId") long eventId);

 @DELETE("api/users/{userId}/favorite-events/{eventId}")
 Call<Void> removeFavoriteEvent(@Path("userId") long userId, @Path("eventId") long eventId);

 @GET("api/users/{userId}/favorite-service-products")
 Call<List<ServiceProductSummaryDto>> getFavoriteServiceProducts(@Path("userId") long userId);

 @POST("api/users/{userId}/favorite-service-products/{serviceProductId}")
 Call<Void> addFavoriteServiceProduct(@Path("userId") long userId, @Path("serviceProductId") long serviceProductId);

 @DELETE("api/users/{userId}/favorite-service-products/{serviceProductId}")
 Call<Void> removeFavoriteServiceProduct(@Path("userId") long userId, @Path("serviceProductId") long serviceProductId);

 @POST("api/users/{userId}/block")
 Call<Void> blockUser(@Path("userId") long userId);

 @DELETE("api/users/{userId}/block")
 Call<Void> unblockUser(@Path("userId") long userId);
}
