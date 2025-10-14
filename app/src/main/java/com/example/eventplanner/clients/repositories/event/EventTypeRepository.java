package com.example.eventplanner.clients.repositories.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.event.EventTypeService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.dto.event.EventTypeDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
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
      eventTypeService.getAllEventTypes().enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<EventType> getEventTypeById(Long id) {
      MutableLiveData<EventType> liveData = new MutableLiveData<>();
      eventTypeService.getEventTypeById(id).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<EventType> createEventType(CreateEventTypeDto dto) {
      MutableLiveData<EventType> liveData = new MutableLiveData<>();
      eventTypeService.createEventType(dto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<EventType> updateEventType(Long id, CreateEventTypeDto dto) {
      MutableLiveData<EventType> liveData = new MutableLiveData<>();
      eventTypeService.updateEventType(id, dto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Boolean> deleteEventType(Long id) {
      MutableLiveData<Boolean> liveData = new MutableLiveData<>();
      Call<ResponseBody> call = eventTypeService.deleteEventType(id);
      call.enqueue(new SimpleCallback<>(
              response -> liveData.setValue(true),
              error -> liveData.setValue(false)
      ));
      return liveData;
   }
}
