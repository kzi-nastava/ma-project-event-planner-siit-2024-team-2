package com.example.eventplanner.fragments.event;

import android.util.Log;

import com.example.eventplanner.clients.repositories.EventRepository;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.model.event.EventType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CreateEventViewModel extends ViewModel {
    private final EventRepository repository;

    private final MutableLiveData<Boolean> isEventCreated = new MutableLiveData<>();

    public CreateEventViewModel() {
        this.repository = new EventRepository();
    }

    public LiveData<Boolean> getIsEventCreated() {
        return isEventCreated;
    }

    public LiveData<List<EventType>> getEventTypes() {
        // delegate to repository
        return repository.getEventTypes();
    }

    public void createEvent(String name, String description, long typeId, int maxAttendances,
                            boolean isOpen, double longitude, double latitude, Date date) {

        boolean isValid = !name.isEmpty() && !description.isEmpty() && typeId > 0;

        if (!isValid) {
            isEventCreated.setValue(false);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(date);

        // Build DTO
        EventDto eventDto = new EventDto(
                name,
                description,
                typeId,
                1,
                maxAttendances,
                isOpen,
                longitude,
                latitude,
                formattedDate,
                new ArrayList<>(), // activityIds
                new ArrayList<>()  // budgetIds
        );

        // delegate to repository
        repository.createEvent(eventDto).observeForever(event -> {
            if (event != null) {
                Log.d("EVENT_CREATED", event.toString());
                isEventCreated.setValue(true);
            } else {
                isEventCreated.setValue(false);
            }
        });
    }
}
