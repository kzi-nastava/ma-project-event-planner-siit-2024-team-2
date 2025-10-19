package com.example.eventplanner.fragments.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.utils.ObserverTracker;

public class ServiceProductDetailsViewModel extends ViewModel {

   private final MutableLiveData<ServiceProduct> serviceProduct = new MutableLiveData<>();
    private final ServiceProductRepository serviceProductRepository = new ServiceProductRepository();
    private final ServiceRepository serviceRepository = new ServiceRepository();
    private final ObserverTracker tracker = new ObserverTracker();

    public LiveData<ServiceProduct> getServiceProductById(long id) {
        tracker.observeOnce(serviceProductRepository.getServiceProductById(id), result -> {
            if (result.getDtype().equals("Service"))
                tracker.observeOnce(serviceRepository.getService(id), serviceProduct::setValue);
            else
                serviceProduct.setValue(result);
        });
        return serviceProduct;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}