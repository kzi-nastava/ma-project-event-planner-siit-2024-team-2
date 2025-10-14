package com.example.eventplanner.fragments.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.EventRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductRepository;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.utils.ObserverTracker;

import java.util.List;

import lombok.Getter;

public class HomeViewModel extends ViewModel {
    private final EventRepository eventRepository = new EventRepository();
    private final ServiceProductRepository serviceProductRepository = new ServiceProductRepository();

    @Getter
    private final MutableLiveData<List<EventSummaryDto>> topEvents = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<ServiceProductSummaryDto>> topServiceProducts = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();

    public void fetchTopEvents() {
        tracker.observeOnce(eventRepository.getTop5(), topEvents, true);
    }
    public void fetchTopServiceProducts() {
        tracker.observeOnce(serviceProductRepository.getTop5(), topServiceProducts, true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
