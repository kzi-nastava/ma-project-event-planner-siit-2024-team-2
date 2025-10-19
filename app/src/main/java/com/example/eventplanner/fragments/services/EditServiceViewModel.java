package com.example.eventplanner.fragments.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductCategoryRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.List;

public class EditServiceViewModel extends ViewModel {
    private final ServiceProductCategoryRepository categoryRepository = new ServiceProductCategoryRepository();
    private final EventTypeRepository eventTypeRepository = new EventTypeRepository();
    private final ServiceRepository serviceRepository = new ServiceRepository();

    private final MutableLiveData<List<ServiceProductCategory>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<EventType>> eventTypes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<Boolean> getSuccess() { return success; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public LiveData<List<ServiceProductCategory>> getCategories() {
        return categoryRepository.getAllServiceProductCategories();
    }

    public LiveData<List<EventType>> getEventTypes() {
        return eventTypeRepository.getAllEventTypes();
    }


    public void createService(ServiceDto dto) {
        Log.d("Service value", dto.toString());
        serviceRepository.createService(dto).observeForever(result -> {
            if (result != null) {
                success.setValue(true);
            } else {
                errorMessage.setValue("Failed to create service!");
            }
        });
    }

    public void updateService(ServiceDto dto) {
        serviceRepository.updateService(dto.getId(), dto).observeForever(success -> {
            if (success != null) {
                this.success.postValue(true);
            } else {
                this.errorMessage.postValue("Failed to update service");
            }
        });
    }

    public LiveData<Service> getService(Long id) {
        return serviceRepository.getService(id);
    }
}
