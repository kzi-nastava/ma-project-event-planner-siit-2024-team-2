package com.example.eventplanner.clients.services.serviceproduct;

import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.model.serviceproduct.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServiceService {

    @GET("services/mine")
    Call<List<Service>> getMyServices();

    @GET("services/{id}")
    Call<Service> getServiceById(@Path("id") long id);

    @POST("services")
    Call<Service> createService(@Body ServiceDto dto);

    @PUT("services/{id}")
    Call<Service> updateService(@Path("id") long id, @Body ServiceDto dto);

    @DELETE("services/{id}")
    Call<Void> deleteService(@Path("id") long id);
}
