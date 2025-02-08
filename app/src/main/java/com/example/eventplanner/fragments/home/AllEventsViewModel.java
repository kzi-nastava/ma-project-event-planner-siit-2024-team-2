package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllEventsViewModel extends ViewModel {
    private final MutableLiveData<String> searchText;
    public AllEventsViewModel(){
        searchText = new MutableLiveData<>();
        searchText.setValue("Search...");
    }
    public LiveData<String> getText(){
        return searchText;
    }
}
