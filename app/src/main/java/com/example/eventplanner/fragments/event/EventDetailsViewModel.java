package com.example.eventplanner.fragments.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventRepository;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.utils.ObserverTracker;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class EventDetailsViewModel extends ViewModel {

    private final EventRepository eventRepository = new EventRepository();
    private final MutableLiveData<Event> event = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();

    public LiveData<Event> getEventById(long eventId) {
        tracker.observeOnce(eventRepository.getEventById(eventId), event, true);
        return event;
    }

    public LiveData<Boolean> isUserAttending(long eventId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        tracker.observeOnce(eventRepository.isUserAttending(eventId), result, true);
        return result;
    }

    public LiveData<Boolean> attendEvent(long eventId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        tracker.observeOnce(eventRepository.attendEvent(eventId), result, true);
        return result;
    }

    public LiveData<Boolean> removeAttendance(long eventId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        tracker.observeOnce(eventRepository.removeAttendance(eventId), result, true);
        return result;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
