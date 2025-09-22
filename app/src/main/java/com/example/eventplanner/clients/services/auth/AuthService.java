package com.example.eventplanner.clients.services.auth;

import com.example.eventplanner.dto.auth.LoginDto;
import com.example.eventplanner.dto.auth.LoginResponseDto;
import com.example.eventplanner.dto.auth.QuickLoginDto;
import com.example.eventplanner.dto.auth.RegisterServiceProductProviderDto;
import com.example.eventplanner.dto.auth.RegisterUserDto;
import com.example.eventplanner.dto.auth.ResetPasswordDto;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.Event;
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

public interface AuthService {
    @Headers({"Content-Type:application/json"})
    @POST("auth/login")
    Call<LoginResponseDto> login(@Body LoginDto loginDto);

    @Headers({"Content-Type:application/json"})
    @POST("auth/signup")
    Call<ResponseBody> registerUser(@Body RegisterUserDto registerUserDto);

    @Headers({"Content-Type:application/json"})
    @POST("auth/signup/company")
    Call<ResponseBody> registerCompany(@Body RegisterServiceProductProviderDto registerCompanyDto);

    @Headers({"Content-Type:application/json"})
    @POST("auth/reset-password/{id}")
    Call<ResponseBody> resetPassword(@Path("id") Long id, @Body ResetPasswordDto resetPasswordDto);

    @Headers({"Content-Type:application/json"})
    @POST("auth/quick-login")
    Call<LoginResponseDto> quickLogin(@Body QuickLoginDto quickLoginDto);
}