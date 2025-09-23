package com.example.eventplanner.clients.services.user;

import android.app.DownloadManager;

import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.dto.user.UserDto;
import com.example.eventplanner.dto.user.UserInfoDto;
import com.example.eventplanner.dto.user.CompanyInfoDto;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ProfileService {

    @POST("users/{userId}/upload-picture")
    Call<UserDto> uploadProfilePicture(@Path("userId") long userId, @Body ImageNameBody body);

    @DELETE("users/{userId}/remove-picture")
    Call<UserDto> removeProfilePicture(@Path("userId") long userId);

    @GET("users/{userId}")
    Call<UserDto> getUserData(@Path("userId") long userId);

    @PUT("users/{userId}")
    Call<UserDto> updatePersonalInfo(@Path("userId") long userId, @Body UserInfoDto body);

    @GET("users/company/{userId}")
    Call<CompanyInfoDto> getCompanyData(@Path("userId") long userId);

    @PUT("users/company/{userId}")
    Call<CompanyInfoDto> updateCompanyInfo(@Path("userId") long userId, @Body CompanyInfoDto body);

    @POST("auth/reset-password/{userId}")
    Call<Void> changePassword(@Path("userId") long userId, @Body PasswordBody body);

    @DELETE("users/{userId}")
    Call<Void> delete(@Path("userId") long userId);

    @PUT("users/event-types")
    Call<Void> updateEventTypes(@Body EventTypesBody body);

    @GET("users/{userId}/favorite-service-products")
    Call<List<ServiceProductSummaryDto>> getFavoriteServiceProducts(@Path("userId") long userId);

    @GET("users/{userId}/favorite-events")
    Call<List<EventSummaryDto>>  getFavoriteEvents(@Path("userId")long userId);

    // Helper body classes
    class ImageNameBody {
        private String imageName;
        public ImageNameBody(String imageName) { this.imageName = imageName; }
        public String getImageName() { return imageName; }
    }

    class PasswordBody {
        private String oldPassword;
        private String newPassword;
        public PasswordBody(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }
    }

    class EventTypesBody {
        private List<String> eventTypes;
        public EventTypesBody(List<String> eventTypes) { this.eventTypes = eventTypes; }
        public List<String> getEventTypes() { return eventTypes; }
    }
}
