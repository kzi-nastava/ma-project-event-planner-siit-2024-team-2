package com.example.eventplanner.clients.services.user;

import com.example.eventplanner.dto.user.UserReportDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserReportService {
    @POST("user-reports")
    Call<ResponseBody> reportUser(@Body UserReportDto reportDto);
}
