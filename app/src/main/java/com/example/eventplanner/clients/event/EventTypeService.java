package com.example.eventplanner.clients.event;

import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.event.EventTypeDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
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

public interface EventTypeService {
    @Headers({"Content-Type:application/json"})
    @GET("event-types")
    Call<PagedModel<EventType>> getAllEventTypes();

    @Headers({"Content-Type:application/json"})
    @GET("event-types/{id}")
    Call<EventType> getEventTypeById(@Path("id") Long id);

    @Headers({"Content-Type:application/json"})
    @POST("event-types")
    Call<EventType> createEventType(@Body EventTypeDto eventTypeDto);

    @Headers({"Content-Type:application/json"})
    @PUT("event-types/{id}")
    Call<EventType> updateEventType(@Path("id") Long id, @Body EventTypeDto eventTypeDto);

    @Headers({"Content-Type:application/json"})
    @DELETE("event-types/{id}")
    Call<ResponseBody> deleteEventType(@Path("id") Long id);
}
