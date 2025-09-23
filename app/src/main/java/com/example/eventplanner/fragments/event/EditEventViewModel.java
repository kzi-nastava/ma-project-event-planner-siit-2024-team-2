package com.example.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class EditEventViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isEventUpdated = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();

    public LiveData<Boolean> getIsEventUpdated() {
        return isEventUpdated;
    }

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

    public LiveData<List<EventType>> getEventTypes() {
        // Fetch event types from server only once
        if (eventTypes.getValue() == null) {
            Call<List<EventType>> call = ClientUtils.eventTypeService.getAllEventTypes();
            call.enqueue(new SimpleCallback<>(
                    response -> eventTypes.setValue(response.body()),
                    error -> eventTypes.setValue(null)
            ));
        }
        return eventTypes;
    }

    public Event getCurrentEvent() {
        return event.getValue();
    }

    public void updateEvent(long eventId, String name, String description, long typeId,
                            int maxAttendances, boolean isOpen,
                            double longitude, double latitude, Date date) {

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(date);

        EventDto dto = new EventDto(name, description, typeId, 1, maxAttendances,
                isOpen, longitude, latitude, formattedDate, null, null);

        Call<Event> call = ClientUtils.eventService.updateEvent(eventId, dto);
        call.enqueue(new SimpleCallback<>(
                response -> isEventUpdated.setValue(true),
                error -> isEventUpdated.setValue(false)
        ));
    }
}
