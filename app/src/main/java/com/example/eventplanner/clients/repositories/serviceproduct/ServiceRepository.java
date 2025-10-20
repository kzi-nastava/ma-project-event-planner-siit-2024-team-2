package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ServiceService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.DateRangeDto;
import com.example.eventplanner.dto.order.BookingDto;
import com.example.eventplanner.dto.order.OrderEligibilityDto;
import com.example.eventplanner.dto.order.PurchaseDto;
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

    public LiveData<List<ServiceDto>> getMyServices() {
        MutableLiveData<List<ServiceDto>> liveData = new MutableLiveData<>();
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

    public LiveData<List<DateRangeDto>> getAvailability(long serviceId, long eventId) {
        MutableLiveData<List<DateRangeDto>> liveData = new MutableLiveData<>();
        serviceService.getAvailability(serviceId, eventId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }

    public LiveData<Pair<Boolean, String>> createBooking(long budgetId, BookingDto bookingDto) {
        MutableLiveData<Pair<Boolean, String>> liveData = new MutableLiveData<>();
        serviceService.createBooking(budgetId, bookingDto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(new Pair<>(true, null)),
                error -> liveData.setValue(new Pair<>(null, error.second))
        ));
        return liveData;
    }

    public LiveData<Pair<Boolean, String>> createPurchase(long budgetId, PurchaseDto purchaseDto) {
        MutableLiveData<Pair<Boolean, String>> liveData = new MutableLiveData<>();
        serviceService.createPurchase(budgetId, purchaseDto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(new Pair<>(true, null)),
                error -> liveData.setValue(new Pair<>(null, error.second))
        ));
        return liveData;
    }

    public LiveData<OrderEligibilityDto> getOrderEligibility(long serviceProductId) {
        MutableLiveData<OrderEligibilityDto> liveData = new MutableLiveData<>();
        serviceService.getOrderEligibility(serviceProductId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}
