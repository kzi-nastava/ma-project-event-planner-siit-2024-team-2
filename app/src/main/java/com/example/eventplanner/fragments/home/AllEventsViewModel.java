package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import lombok.Setter;

public class AllEventsViewModel extends ViewModel {
    private final MutableLiveData<String> queryHint;
    private final MutableLiveData<String> searchText;
    @Getter
    @Setter
    private int currentPage;
    public AllEventsViewModel(){
        searchText = new MutableLiveData<>();
        queryHint = new MutableLiveData<>();
        queryHint.setValue("Search...");
    }
    public LiveData<String> getHint(){
        return queryHint;
    }
    public void setSearchText(String value){
        searchText.setValue(value);
    }
    public LiveData<String> getSearchText(){
        return searchText;
    }
}
