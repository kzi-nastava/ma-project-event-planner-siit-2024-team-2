package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.user.ProfileRepository;

import lombok.Getter;
import lombok.Setter;

public class AllServiceProductsViewModel extends ViewModel {
    private final ProfileRepository profileRepository = new ProfileRepository();
    private final MutableLiveData<String> queryHint;
    private final MutableLiveData<String> searchText;
    @Getter
    @Setter
    private int currentPage = 1;
    public AllServiceProductsViewModel(){
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

    private final MutableLiveData<Boolean> favoriteActionSuccess = new MutableLiveData<>();
    public LiveData<Boolean> getFavoriteActionSuccess() { return favoriteActionSuccess; }

    // Favorite service products
    public void addFavoriteServiceProduct(long userId, long productId) {
        profileRepository.addFavoriteServiceProduct(userId, productId).observeForever(favoriteActionSuccess::setValue);
    }

    public void removeFavoriteServiceProduct(long userId, long productId) {
        profileRepository.removeFavoriteServiceProduct(userId, productId).observeForever(favoriteActionSuccess::setValue);
    }
}
