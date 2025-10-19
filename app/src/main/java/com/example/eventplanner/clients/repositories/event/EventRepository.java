package com.example.eventplanner.clients.repositories.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.event.EventService;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class EventRepository {

   private final EventService eventService;

   public EventRepository() {
      this.eventService = ClientUtils.eventService;
   }

   public LiveData<PagedModel<Event>> getEvents(
           Integer page, Integer size, String sortBy, SortDirection sortDirection,
           String name, String description, List<Long> types,
           Integer minMaxAttendances, Integer maxMaxAttendances, Boolean open,
           List<Double> latitudes, List<Double> longitudes, Double maxDistance,
           Long startDate, Long endDate) {

      MutableLiveData<PagedModel<Event>> liveData = new MutableLiveData<>();
      Call<PagedModel<Event>> call = eventService.getEvents(page, size, sortBy, sortDirection,
              name, description, types, minMaxAttendances, maxMaxAttendances, open,
              latitudes, longitudes, maxDistance, startDate, endDate);

      call.enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<PagedModel<EventSummaryDto>> getEventSummaries(
           Integer page, Integer size, String sortBy, SortDirection sortDirection,
           String name, String description, List<Long> types,
           Integer minMaxAttendances, Integer maxMaxAttendances, Boolean open,
           List<Double> latitudes, List<Double> longitudes, Double maxDistance,
           Long startDate, Long endDate) {

      MutableLiveData<PagedModel<EventSummaryDto>> liveData = new MutableLiveData<>();
      Call<PagedModel<EventSummaryDto>> call = eventService.getEventSummaries(page, size, sortBy, sortDirection,
              name, description, types, minMaxAttendances, maxMaxAttendances, open,
              latitudes, longitudes, maxDistance, startDate, endDate);

      call.enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Event> getEventById(Long id) {
      MutableLiveData<Event> liveData = new MutableLiveData<>();
      eventService.getEventById(id).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Event> createEvent(EventDto eventDto) {
      MutableLiveData<Event> liveData = new MutableLiveData<>();
      eventService.createEvent(eventDto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Event> updateEvent(Long id, EventDto eventDto) {
      MutableLiveData<Event> liveData = new MutableLiveData<>();
      eventService.updateEvent(id, eventDto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Boolean> deleteEvent(Long id) {
      MutableLiveData<Boolean> liveData = new MutableLiveData<>();
      eventService.deleteEvent(id).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(true),
              error -> liveData.setValue(false)
      ));
      return liveData;
   }

   public LiveData<List<EventSummaryDto>> getTop5() {
      MutableLiveData<List<EventSummaryDto>> liveData = new MutableLiveData<>();
      eventService.getTop5().enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<List<Integer>> getMaxAttendancesRange() {
      MutableLiveData<List<Integer>> liveData = new MutableLiveData<>();
      eventService.getMaxAttendancesRange().enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<List<EventType>> getEventTypes() {
      MutableLiveData<List<EventType>> liveData = new MutableLiveData<>();
      ClientUtils.eventTypeService.getAllEventTypes().enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }
   public LiveData<Boolean> isUserAttending(long eventId) {
      MutableLiveData<Boolean> result = new MutableLiveData<>();
      Call<Boolean> call = eventService.isUserAttending(eventId);

      call.enqueue(new SimpleCallback<>(
              response -> result.setValue(response != null && response.body() != null ? response.body() : false),
              error -> result.setValue(false)
      ));
      return result;
   }

   public LiveData<Boolean> attendEvent(long eventId) {
      MutableLiveData<Boolean> result = new MutableLiveData<>();
      Call<Void> call = eventService.attendEvent(eventId);

      call.enqueue(new SimpleCallback<>(
              response -> result.setValue(response != null && response.isSuccessful()),
              error -> result.setValue(false)
      ));
      return result;
   }

   public LiveData<Boolean> removeAttendance(long eventId) {
      MutableLiveData<Boolean> result = new MutableLiveData<>();
      Call<Void> call = eventService.removeAttendance(eventId);

      call.enqueue(new SimpleCallback<>(
              response -> result.setValue(response != null && response.isSuccessful()),
              error -> result.setValue(false)
      ));
      return result;
   }


}
