package com.example.eventplanner.clients.event;

import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;

import java.util.ArrayList;
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

public interface EventService {
    @Headers({"Content-Type:application/json"})
    @GET("events")
    Call<PagedModel<Event>> getEvents(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") SortDirection sortDirection,
            @Query("name") String name,
            @Query("description") String description,
            @Query("types") List<Long> types,
            @Query("minMaxAttendances") Integer minMaxAttendances,
            @Query("maxMaxAttendances") Integer maxMaxAttendances,
            @Query("open") Boolean open,
            @Query("latitudes") List<Double> latitudes,
            @Query("longitudes") List<Double> longitudes,
            @Query("maxDistance") Double maxDistance,
            @Query("startDate") Long startDate,
            @Query("endDate") Long endDate);

    @Headers({"Content-Type:application/json"})
    @GET("events/summaries")
    Call<PagedModel<EventSummaryDto>> getEventSummaries(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sortBy") String sortBy,
            @Query("sortDirection") SortDirection sortDirection,
            @Query("name") String name,
            @Query("description") String description,
            @Query("types") List<Long> types,
            @Query("minMaxAttendances") Integer minMaxAttendances,
            @Query("maxMaxAttendances") Integer maxMaxAttendances,
            @Query("open") Boolean open,
            @Query("latitudes") List<Double> latitudes,
            @Query("longitudes") List<Double> longitudes,
            @Query("maxDistance") Double maxDistance,
            @Query("startDate") Long startDate,
            @Query("endDate") Long endDate);

    @Headers({"Content-Type:application/json"})
    @GET("events/{id}")
    Call<Event> getEventById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("events")
    Call<Event> createEvent(@Body EventDto eventDto);

    @Headers({"Content-Type:application/json"})
    @PUT("events/{id}")
    Call<Event> updateEvent(@Path("id") Long id, @Body EventDto eventDto);

    @Headers({"Content-Type:application/json"})
    @DELETE("events/{id}")
    Call<ResponseBody> deleteEvent(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @GET("events/top5")
    Call<List<EventSummaryDto>> getTop5();

    @Headers({"Content-Type:application/json"})
    @GET("events/max-attendances-range")
    Call<List<Integer>> getMaxAttendancesRange();
}
