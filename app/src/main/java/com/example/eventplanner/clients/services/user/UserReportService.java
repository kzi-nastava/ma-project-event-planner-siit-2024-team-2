package com.example.eventplanner.clients.services.user;

import com.example.eventplanner.dto.user.UserReportDto;
import com.example.eventplanner.model.user.UserReport;
import com.example.eventplanner.model.utils.PagedModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserReportService {
    @POST("user-reports")
    Call<ResponseBody> reportUser(@Body UserReportDto reportDto);
    
    @GET("user-reports/not-approved")
    Call<PagedModel<UserReport>> getAllNotApproved(@Query("page") Integer page, @Query("size") Integer size);
    
    @POST("user-reports/{id}/approve")
    Call<UserReport> approve(@Path("id") Long id);
    
    @DELETE("user-reports/{id}")
    Call<Boolean> delete(@Path("id") Long id);
}
