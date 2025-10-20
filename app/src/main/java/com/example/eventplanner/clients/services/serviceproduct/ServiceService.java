package com.example.eventplanner.clients.services.serviceproduct;

import com.example.eventplanner.dto.event.DateRangeDto;
import com.example.eventplanner.dto.order.BookingDto;
import com.example.eventplanner.dto.order.OrderEligibilityDto;
import com.example.eventplanner.dto.order.PurchaseDto;
import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.model.serviceproduct.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceService {

    @GET("services/mine")
    Call<List<ServiceDto>> getMyServices();

    @GET("services/{id}")
    Call<Service> getServiceById(@Path("id") long id);

    @POST("services")
    Call<Service> createService(@Body ServiceDto dto);

    @PUT("services/{id}")
    Call<Service> updateService(@Path("id") long id, @Body ServiceDto dto);

    @DELETE("services/{id}")
    Call<Void> deleteService(@Path("id") long id);

    @Headers({"Content-Type:application/json"})
    @GET("services/{serviceId}/availability")
    Call<List<DateRangeDto>> getAvailability(
            @Path("serviceId") long serviceId,
            @Query("eventId") long eventId
    );

    @Headers({"Content-Type:application/json"})
    @POST("budgets/{budgetId}/bookings")
    Call<Void> createBooking(@Path("budgetId") long budgetId, @Body BookingDto bookingDto);

    @Headers({"Content-Type:application/json"})
    @POST("budgets/{budgetId}/purchases")
    Call<Void> createPurchase(@Path("budgetId") long budgetId, @Body PurchaseDto purchaseDto);

    @Headers({"Content-Type:application/json"})
    @GET("service-products/{id}/order-eligibility")
    Call<OrderEligibilityDto> getOrderEligibility(@Path("id") long id);
}
