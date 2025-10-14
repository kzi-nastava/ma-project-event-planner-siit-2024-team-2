package com.example.eventplanner.clients.services.serviceproduct;

import com.example.eventplanner.dto.serviceproduct.ProductDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ProductService {

    @GET("products/mine")
    Call<List<ProductDto>> getMyProducts();

    @POST("products")
    Call<ProductDto> createProduct(@Body ProductDto dto);

    @PUT("products/{id}")
    Call<ProductDto> updateProduct(@Path("id") long id, @Body ProductDto dto);

    @DELETE("products/{id}")
    Call<Void> deleteProduct(@Path("id") long id);
}
