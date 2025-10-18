package com.example.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.eventplanner.clients.repositories.event.EventRepository;
import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.ObserverTracker;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class EditEventViewModel extends ViewModel {

    private final EventRepository eventRepository = new EventRepository();
    private final EventTypeRepository eventTypeRepository = new EventTypeRepository();
    private final MutableLiveData<Boolean> isEventUpdated = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();

    public LiveData<Boolean> getIsEventUpdated() {
        return isEventUpdated;
    }
    private final ObserverTracker tracker = new ObserverTracker();

    public LiveData<Event> getEventById(long eventId) {
        tracker.observeOnce(eventRepository.getEventById(eventId), event, true);
        return event;
    }

    public LiveData<List<EventType>> getEventTypes() {
        tracker.observeOnce(eventTypeRepository.getAllEventTypes(), eventTypes, true);
        return eventTypes;
    }

    public Event getCurrentEvent() {
        return event.getValue();
    }

    public void updateEvent(long eventId, String name, String description, long typeId,
                            int maxAttendances, boolean isOpen,
                            double longitude, double latitude, Date date, List<String> emails) {

        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(date);

        EventDto dto = new EventDto(name, description, typeId, 1, maxAttendances,  // backend uses authenticated user's id
                isOpen, longitude, latitude, formattedDate, null, null, emails);

        tracker.observeOnce(eventRepository.updateEvent(eventId, dto),
                result -> isEventUpdated.setValue(result != null));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
