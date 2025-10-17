package com.example.eventplanner.clients.services.event;

import com.example.eventplanner.dto.event.ActivityDto;
import com.example.eventplanner.model.event.Activity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AgendaService {

    /**
     * GET /events/{id}/agenda
     * Fetch all activities (agenda) for an event.
     */
    @GET("events/{id}/agenda")
    Call<List<ActivityDto>> getAgenda(@Path("id") long eventId);

    /**
     * POST /events/{id}/agenda
     * Replace the entire agenda (bulk create).
     */
    @POST("events/{id}/agenda")
    Call<List<ActivityDto>> createAgenda(
            @Path("id") long eventId,
            @Body List<ActivityDto> activities
    );

    /**
     * POST /events/{id}/agenda/activity
     * Add a single new activity.
     */
    @POST("events/{id}/agenda/activity")
    Call<ActivityDto> addActivity(
            @Path("id") long eventId,
            @Body ActivityDto activity
    );

    /**
     * PUT /events/{id}/agenda/activity/{activityId}
     * Update an existing activity.
     */
    @PUT("events/{id}/agenda/activity/{activityId}")
    Call<ActivityDto> updateActivity(
            @Path("id") long eventId,
            @Path("activityId") long activityId,
            @Body ActivityDto activity
    );

    /**
     * DELETE /events/{id}/agenda/activity/{activityId}
     * Delete a specific activity.
     */
    @DELETE("events/{id}/agenda/activity/{activityId}")
    Call<Void> deleteActivity(
            @Path("id") long eventId,
            @Path("activityId") long activityId
    );
}
