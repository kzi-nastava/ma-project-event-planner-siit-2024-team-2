package com.example.eventplanner.fragments.event;

import android.util.Log;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;

public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isEventCreated = new MutableLiveData<>();

    public LiveData<Boolean> getIsEventCreated() {
        return isEventCreated;
    }

    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();

    public LiveData<List<EventType>> getEventTypes() {
        Call<List<EventType>> call = ClientUtils.eventTypeService.getAllEventTypes();

        call.enqueue(new SimpleCallback<>(
                response -> {
                    if (response != null) {
                        eventTypes.setValue(response.body());
                    }
                },
                error -> {
                    // You might want to log or expose error state
                    eventTypes.setValue(new ArrayList<>()); // fallback empty list
                }
        ));

        return eventTypes;
    }


    public void createEvent(String name, String description, long typeId, int maxAttendances,
                            boolean isOpen, double longitude, double latitude, Date date) {

        boolean isValid = !name.isEmpty() && !description.isEmpty() && typeId > 0;

        if (!isValid) {
            isEventCreated.setValue(false);
            return;
        }

        // Build DTO
        EventDto eventDto = new EventDto(
                name,
                description,
                typeId,
                maxAttendances,
                isOpen,
                longitude,
                latitude,
                date,
                new ArrayList<>(), // activityIds
                new ArrayList<>()  // budgetIds
        );

        // Call backend
        Call<Event> call = ClientUtils.eventService.createEvent(eventDto);

        call.enqueue(new SimpleCallback<>(
                response -> {
                    if (response != null && response.body() != null) {
                        Log.d("EVENT_CREATED", response.body().toString());
                        isEventCreated.setValue(true);
                    } else {
                        isEventCreated.setValue(false);
                    }
                },
                error -> {
                    Log.e("EVENT_CREATE_ERROR", error.toString());
                    isEventCreated.setValue(false);
                }
        ));
    }

}