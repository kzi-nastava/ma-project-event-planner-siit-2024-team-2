package com.example.eventplanner.fragments.example;

import androidx.lifecycle.ViewModel;

import com.example.eventplanner.utils.ObserverTracker;

public class ExampleViewModel extends ViewModel {
    private final ObserverTracker tracker = new ObserverTracker();

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
