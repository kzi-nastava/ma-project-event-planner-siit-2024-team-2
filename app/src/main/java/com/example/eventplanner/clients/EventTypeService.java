package com.example.eventplanner.clients;


import com.example.eventplanner.dto.EventTypeDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventTypeService {
    @GET("/api/event-types")
    Call<List<EventTypeDto>> getEventTypes();
}