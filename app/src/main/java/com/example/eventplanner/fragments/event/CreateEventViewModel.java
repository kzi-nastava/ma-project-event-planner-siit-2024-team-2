package com.example.eventplanner.fragments.event;

import com.example.eventplanner.clients.EventTypeService;
import com.example.eventplanner.dto.EventTypeDto;
import com.example.eventplanner.model.event.Activity;
import com.example.eventplanner.model.event.Budget;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.repositories.EventTypeRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isEventCreated = new MutableLiveData<>();
    private final EventTypeRepository repository = new EventTypeRepository();

    public LiveData<Boolean> getIsEventCreated() {
        return isEventCreated;
    }

    public LiveData<List<EventTypeDto>> getEventTypes() {
        return repository.getEventTypes();
    }

    public void createEvent(String name, String description, long typeId, int maxAttendances,
                            boolean isOpen, double longitude, double latitude, Date date) {

        boolean isValid = !name.isEmpty() && !description.isEmpty() && typeId > 0;

        if (isValid) {
            Event event = new Event(-1L, name, description, new EventType(), maxAttendances, isOpen, latitude, longitude, date, new ArrayList<Activity>(), new ArrayList<Budget>());
            isEventCreated.setValue(true);
        } else {
            isEventCreated.setValue(false);
        }
    }
}