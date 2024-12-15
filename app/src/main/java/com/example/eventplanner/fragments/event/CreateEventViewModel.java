package com.example.eventplanner.fragments.event;

import com.example.eventplanner.model.event.Activity;
import com.example.eventplanner.model.event.Budget;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;

import java.util.ArrayList;
import java.util.Date;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isEventCreated = new MutableLiveData<>();

    public LiveData<Boolean> getIsEventCreated() {
        return isEventCreated;
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