package com.example.eventplanner.clients.services.serviceproduct;

import com.example.eventplanner.dto.serviceproduct.PriceListDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PriceListService {
    @GET("price-list/{sppId}")
    Call<List<PriceListDto>> getBySppId(@Path("sppId") long sppId);

    @PUT
    Call<PriceListDto> update(
            @Path("id") long id,
            @Query("price") double price,
            @Query("discount") double discount
    );

    @GET("price-list/{sppId}/pdf")
    Call<ResponseBody> downloadPdf(@Path("sppId") long sppId);
}
