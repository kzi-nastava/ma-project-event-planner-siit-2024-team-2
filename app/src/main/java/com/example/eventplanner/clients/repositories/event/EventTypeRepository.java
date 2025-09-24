package com.example.eventplanner.clients.repositories.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.event.EventTypeService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventTypeDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class EventTypeRepository {

    private final EventTypeService eventTypeService;

    public EventTypeRepository() {
        this.eventTypeService = ClientUtils.eventTypeService;
    }

    public LiveData<List<EventType>> getAllEventTypes() {
        MutableLiveData<List<EventType>> liveData = new MutableLiveData<>();
        Call<List<EventType>> call = eventTypeService.getAllEventTypes();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<EventType> getEventTypeById(Long id) {
        MutableLiveData<EventType> liveData = new MutableLiveData<>();
        Call<EventType> call = eventTypeService.getEventTypeById(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<EventType> createEventType(EventTypeDto eventTypeDto) {
        MutableLiveData<EventType> liveData = new MutableLiveData<>();
        Call<EventType> call = eventTypeService.createEventType(eventTypeDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<EventType> updateEventType(Long id, EventTypeDto eventTypeDto) {
        MutableLiveData<EventType> liveData = new MutableLiveData<>();
        Call<EventType> call = eventTypeService.updateEventType(id, eventTypeDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteEventType(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = eventTypeService.deleteEventType(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}