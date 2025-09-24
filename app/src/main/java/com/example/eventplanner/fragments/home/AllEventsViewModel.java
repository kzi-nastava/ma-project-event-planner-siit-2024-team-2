package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.dto.event.EventSummaryDto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AllEventsViewModel extends ViewModel {
    private final ProfileRepository profileRepository = new ProfileRepository();
    private final MutableLiveData<String> queryHint = new MutableLiveData<>("Search...");
    private final MutableLiveData<String> searchText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> favoriteActionSuccess = new MutableLiveData<>();
    private final MutableLiveData<List<Long>> favoriteEventIds = new MutableLiveData<>(new ArrayList<>());
    @Setter
    private int currentPage = 1;

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
}
