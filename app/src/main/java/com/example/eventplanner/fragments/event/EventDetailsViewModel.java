package com.example.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class EventDetailsViewModel extends ViewModel {

   private final MutableLiveData<Event> event = new MutableLiveData<>();

   public LiveData<Event> getEventById(long eventId) {
      Call<Event> call = ClientUtils.eventService.getEventById(eventId);
      call.enqueue(new SimpleCallback<>(
              response -> {
                 if (response != null) {
                    event.setValue(response.body());
                 }
              },
              error -> event.setValue(null)
      ));
      return event;
   }
}
