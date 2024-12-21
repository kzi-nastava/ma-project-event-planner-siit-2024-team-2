package com.example.eventplanner.clients;

import com.example.eventplanner.model.order.Booking;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookingService {
    @Headers({"Content-Type:application/json"})
    @GET("bookings")
    Call<ArrayList<Booking>> getAll();

    @Headers({"Content-Type:application/json"})
    @GET("bookings/{id}")
    Call<Booking> getById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("bookings")
    Call<Booking> create(@Body Booking booking);

    @Headers({"Content-Type:application/json"})
    @PUT("bookings/{id}")
    Call<Booking> update(@Path("id") Long id, @Body Booking booking);

    @Headers({"Content-Type:application/json"})
    @DELETE("bookings/{id}")
    Call<ResponseBody> delete(@Path("id") Long id);
}
