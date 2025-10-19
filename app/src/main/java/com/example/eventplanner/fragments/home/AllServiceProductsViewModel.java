package com.example.eventplanner.fragments.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductRepository;
import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.dto.serviceproduct.ServiceProductFilteringValuesDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.ServiceProductDType;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.utils.ObserverTracker;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class AllServiceProductsViewModel extends ViewModel {
    private final ProfileRepository profileRepository = new ProfileRepository();
    private final ServiceProductRepository serviceProductRepository = new ServiceProductRepository();
    private final MutableLiveData<String> queryHint;
    private final MutableLiveData<String> searchText;
    @Getter
    @Setter
    private int currentPage = 1;

    @Getter
    private final MutableLiveData<PagedModel<ServiceProductSummaryDto>> serviceProducts = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<ServiceProductFilteringValuesDto> filteringValues = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();
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
        tracker.observeOnce(profileRepository.addFavoriteServiceProduct(userId, productId), favoriteActionSuccess::setValue);
    }

    public void removeFavoriteServiceProduct(long userId, long productId) {
        tracker.observeOnce(profileRepository.removeFavoriteServiceProduct(userId, productId), favoriteActionSuccess::setValue);
    }

    public void fetchServiceProductSummaries(
            Integer page, Integer size, String sortBy, SortDirection sortDirection, String name,
            String description, ServiceProductDType type, List<Long> categoryIds, Boolean available,
            Boolean visible, Integer minPrice, Integer maxPrice, List<Long> availableEventTypeIds,
            Long serviceProductProviderId, Float minDuration, Float maxDuration, Boolean automaticReserved) {
        tracker.observeOnce(
            serviceProductRepository.getServiceProductSummaries(page, size, sortBy, sortDirection, name,
                    description, type, categoryIds, available, visible, minPrice, maxPrice,
                    availableEventTypeIds, serviceProductProviderId, minDuration, maxDuration,
                    automaticReserved), serviceProducts, true);
    }

    public void fetchFilteringValues() {
        tracker.observeOnce(serviceProductRepository.getFilteringValues(), filteringValues, true);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
