package com.example.eventplanner.fragments.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import java.util.List;

public class ServicesViewModel extends ViewModel {
    private final ServiceRepository repository;
    private final MutableLiveData<List<ServiceDto>> servicesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    public LiveData<Boolean> getDeleteSuccess() { return deleteSuccess; }


    public ServicesViewModel(ServiceRepository repository) {
        this.repository = repository;
        loadServices();
    }

    public LiveData<List<ServiceDto>> getServices() {
        return servicesLiveData;
    }

    public void loadServices() {
        // Observe repository data
        repository.getMyServices().observeForever(services -> {
            servicesLiveData.setValue(services);
        });
    }

    public void deleteService(long id) {
        repository.deleteService(id).observeForever(deleteSuccess::setValue);
    }
}

