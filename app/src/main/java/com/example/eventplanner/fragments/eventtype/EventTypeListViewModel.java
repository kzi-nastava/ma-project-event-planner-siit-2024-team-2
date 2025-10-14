package com.example.eventplanner.fragments.eventtype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.model.event.EventType;

import java.util.List;

public class EventTypeListViewModel extends ViewModel {

    private final EventTypeRepository eventTypeRepository;
    private final MutableLiveData<List<EventType>> eventTypesLiveData;

    public EventTypeListViewModel() {
        this.eventTypeRepository = new EventTypeRepository();
        this.eventTypesLiveData = new MutableLiveData<>();
    }

    /**
     * Fetch all event types from repository
     */
    public void loadEventTypes() {
        LiveData<List<EventType>> repoLiveData = eventTypeRepository.getAllEventTypes();
        repoLiveData.observeForever(eventTypes -> {
            if (eventTypes != null) {
                eventTypesLiveData.setValue(eventTypes);
            }
        });
    }

    /**
     * Expose immutable LiveData for UI
     */
    public LiveData<List<EventType>> getEventTypes() {
        return eventTypesLiveData;
    }

    /**
     * Fetch single event type
     */
    public LiveData<EventType> getEventTypeById(Long id) {
        return eventTypeRepository.getEventTypeById(id);
    }

    /**
     * Create event type
     */
    public LiveData<EventType> createEventType(CreateEventTypeDto eventType) {
        return eventTypeRepository.createEventType(eventType);
    }

    /**
     * Update event type
     */
    public LiveData<EventType> updateEventType(Long id, CreateEventTypeDto eventType) {
        return eventTypeRepository.updateEventType(id, eventType);
    }

    /**
     * Delete event type
     */
    public LiveData<Boolean> deleteEventType(Long id) {
        return eventTypeRepository.deleteEventType(id);
    }
}
