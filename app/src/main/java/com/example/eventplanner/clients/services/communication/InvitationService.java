package com.example.eventplanner.clients.services.communication;

import com.example.eventplanner.model.event.Invitation;
import com.example.eventplanner.dto.event.InvitationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface InvitationService {

    @Headers({"Content-Type:application/json"})
    @GET("invitations")
    Call<List<Invitation>> getAllInvitations();

    @Headers({"Content-Type:application/json"})
    @GET("invitations/{id}")
    Call<Invitation> getInvitation(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @PUT("invitations/{id}")
    Call<Invitation> updateInvitation(@Path("id") Long id, @Body InvitationDto dto);

    @Headers({"Content-Type:application/json"})
    @POST("invitations/{token}/accept")
    Call<Invitation> acceptInvitation(@Path("token") String token);
}
