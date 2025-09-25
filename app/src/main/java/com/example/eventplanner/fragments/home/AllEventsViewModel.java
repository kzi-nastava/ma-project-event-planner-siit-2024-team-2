package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.EventRepository;
import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AllEventsViewModel extends ViewModel {
    private final ProfileRepository profileRepository = new ProfileRepository();
    private final EventRepository eventRepository = new EventRepository();
    private final MutableLiveData<String> queryHint = new MutableLiveData<>("Search...");
    private final MutableLiveData<String> searchText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favoriteActionSuccess = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> favoriteEventIds = new MutableLiveData<>(new ArrayList<>());
    @Setter
    private int currentPage = 1;

    private final MutableLiveData<PagedModel<EventSummaryDto>> events = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Integer>> maxAttendancesRange = new MutableLiveData<>(new ArrayList<>());

    public LiveData<String> getHint() { return queryHint; }
    public LiveData<String> getSearchText() { return searchText; }
    public void setSearchText(String value){ searchText.setValue(value); }

    // Load all favorites for user
    public void loadFavorites(long userId) {
        profileRepository.getFavoriteEvents(userId)
                .observeForever(events -> {
                    if (events != null) {
                        List<Long> ids = new ArrayList<>();
                        for (var e : events) ids.add(e.getId());
                        favoriteEventIds.setValue(ids);
                    }
                });
    }

    public void addFavoriteEvent(long userId, long eventId) {
        profileRepository.addFavoriteEvent(userId, eventId)
                .observeForever(success -> {
                    favoriteActionSuccess.setValue(success);
                    if (success) {
                        List<Long> ids = new ArrayList<>(favoriteEventIds.getValue());
                        if (!ids.contains(eventId)) ids.add(eventId);
                        favoriteEventIds.setValue(ids);
                    }
                });
    }

    public void removeFavoriteEvent(long userId, long eventId) {
        profileRepository.removeFavoriteEvent(userId, eventId)
                .observeForever(success -> {
                    favoriteActionSuccess.setValue(success);
                    if (success) {
                        List<Long> ids = new ArrayList<>(favoriteEventIds.getValue());
                        ids.remove(eventId);
                        favoriteEventIds.setValue(ids);
                    }
                });
    }
    public void fetchEvents(Integer page, Integer size,
                            String sortBy, SortDirection sortDirection, String name, String description,
                            List<Long> types, Integer minMaxAttendances, Integer maxMaxAttendances, Boolean open,
                            List<Double> latitudes, List<Double> longitudes, Double maxDistance, Long startDate,
                            Long endDate) {
        eventRepository.getEventSummaries(page, size, sortBy, sortDirection,
                name, description, types, minMaxAttendances, maxMaxAttendances, open,
                latitudes, longitudes, maxDistance, startDate, endDate
        ).observeForever(result -> {
            if (result != null)
                events.setValue(result);
        });
    }

    public void fetchEventTypes() {
        eventRepository.getEventTypes().observeForever(list -> {
            if (list != null) eventTypes.setValue(list);
        });
    }

    public void fetchAttendancesRange() {
        eventRepository.getMaxAttendancesRange().observeForever(range -> {
            if (range != null) maxAttendancesRange.setValue(range);
        });
    }
}
