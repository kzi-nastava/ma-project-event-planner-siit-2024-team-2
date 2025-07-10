package com.example.eventplanner.clients.serviceproduct;

import com.example.eventplanner.dto.serviceproduct.ServiceProductFilteringValuesDto;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.dto.serviceproduct.ServiceProductDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
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

public interface ServiceProductService {
    @Headers({"Content-Type:application/json"})
    @GET("service-products")
    Call<PagedModel<ServiceProduct>> getServiceProducts(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") SortDirection sortDirection,
            @Query("name") String name,
            @Query("description") String description,
            @Query("categoryIds") List<Long> categoryIds,
            @Query("available") Boolean available,
            @Query("visible") Boolean visible,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("availableEventTypeIds") List<Long> availableEventTypeIds,
            @Query("serviceProductProviderId") Long serviceProductProviderId);

    @Headers({"Content-Type:application/json"})
    @GET("service-products/summaries")
    Call<PagedModel<ServiceProductSummaryDto>> getServiceProductSummaries(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") SortDirection sortDirection,
            @Query("name") String name,
            @Query("description") String description,
            @Query("categoryIds") List<Long> categoryIds,
            @Query("available") Boolean available,
            @Query("visible") Boolean visible,
            @Query("minPrice") Integer minPrice,
            @Query("maxPrice") Integer maxPrice,
            @Query("availableEventTypeIds") List<Long> availableEventTypeIds,
            @Query("serviceProductProviderId") Long serviceProductProviderId);

    @Headers({"Content-Type:application/json"})
    @GET("service-products/{id}")
    Call<ServiceProduct> getServiceProductById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("service-products")
    Call<ServiceProduct> createServiceProduct(@Body ServiceProductDto serviceProductDto);

    @Headers({"Content-Type:application/json"})
    @PUT("service-products/{id}")
    Call<ServiceProduct> updateServiceProduct(@Path("id") Long id, @Body ServiceProductDto serviceProductDto);

    @Headers({"Content-Type:application/json"})
    @DELETE("service-products/{id}")
    Call<ResponseBody> deleteServiceProduct(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @GET("service-products/top5")
    Call<List<ServiceProductSummaryDto>> getTop5();

    @Headers({"Content-Type:application/json"})
    @GET("service-products/filtering-values")
    Call<ServiceProductFilteringValuesDto> getFilteringValues();
}
