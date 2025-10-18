package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ServiceService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {

    private final ServiceService serviceService;

    public ServiceRepository() {
        this.serviceService = ClientUtils.serviceService;
    }

    public LiveData<List<Service>> getMyServices() {
        MutableLiveData<List<Service>> liveData = new MutableLiveData<>();
        serviceService.getMyServices().enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }

    public LiveData<Service> createService(ServiceDto dto) {
        MutableLiveData<Service> liveData = new MutableLiveData<>();
        serviceService.createService(dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Service> updateService(Long id, ServiceDto dto) {
        MutableLiveData<Service> liveData = new MutableLiveData<>();
        serviceService.updateService(id, dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteService(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        serviceService.deleteService(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(true),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<Service> getService(Long id) {
        MutableLiveData<Service> liveData = new MutableLiveData<>();
        serviceService.getServiceById(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}
