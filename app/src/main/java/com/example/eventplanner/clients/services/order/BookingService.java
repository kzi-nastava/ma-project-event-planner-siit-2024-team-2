package com.example.eventplanner.clients.services.order;

import com.example.eventplanner.dto.order.BookingDto;
import com.example.eventplanner.dto.order.PendingBookingDto;
import com.example.eventplanner.model.order.Booking;
import com.example.eventplanner.model.utils.PagedModel;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingService {
    @Headers({"Content-Type:application/json"})
    @GET("bookings")
    Call<List<Booking>> getBookings();

    @Headers({"Content-Type:application/json"})
    @GET("bookings/{id}")
    Call<Booking> getBookingById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("bookings")
    Call<Booking> createBooking(@Body BookingDto bookingDto);

    @Headers({"Content-Type:application/json"})
    @PUT("bookings/{id}")
    Call<Booking> updateBooking(@Path("id") Long id, @Body BookingDto bookingDto);

    @Headers({"Content-Type:application/json"})
    @DELETE("bookings/{id}")
    Call<ResponseBody> deleteBooking(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @GET("bookings/mine")
    Call<PagedModel<PendingBookingDto>> getMyBookings(
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @Headers({"Content-Type:application/json"})
    @POST("bookings/{id}/accept")
    Call<Void> acceptBooking(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @DELETE("bookings/{id}")
    Call<Boolean> declineBooking(@Path("id") Long id);
}
