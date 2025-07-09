package com.example.eventplanner.clients.serviceproduct;

import com.example.eventplanner.dto.serviceproduct.ServiceProductCategoryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;

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

public interface ServiceProductCategoryService {
    @Headers({"Content-Type:application/json"})
    @GET("sp-categories")
    Call<List<ServiceProductCategory>> getAllServiceProductCategories();

    @Headers({"Content-Type:application/json"})
    @GET("sp-categories/{id}")
    Call<ServiceProductCategory> getServiceProductCategoryById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("sp-categories")
    Call<ServiceProductCategory> createServiceProductCategory(@Body ServiceProductCategoryDto serviceProductCategoryDto);

    @Headers({"Content-Type:application/json"})
    @PUT("sp-categories/{id}")
    Call<ServiceProductCategory> updateServiceProductCategory(@Path("id") Long id, @Body ServiceProductCategoryDto serviceProductCategoryDto);

    @Headers({"Content-Type:application/json"})
    @DELETE("sp-categories/{id}")
    Call<ResponseBody> deleteServiceProductCategory(@Path("id") Long id);
}
