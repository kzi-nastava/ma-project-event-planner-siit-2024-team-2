package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ServiceProductService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceProductDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductFilteringValuesDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.ServiceProductDType;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ServiceProductRepository {

    private final ServiceProductService serviceProductService;

    public ServiceProductRepository() {
        this.serviceProductService = ClientUtils.serviceProductService;
    }

    public LiveData<PagedModel<ServiceProduct>> getServiceProducts(
            Integer page, Integer size, String sortBy, SortDirection sortDirection, String name,
            String description, ServiceProductDType type, List<Long> categoryIds, Boolean available,
            Boolean visible, Integer minPrice, Integer maxPrice, List<Long> availableEventTypeIds,
            Long serviceProductProviderId, Float minDuration, Float maxDuration, Boolean automaticReserved) {

        MutableLiveData<PagedModel<ServiceProduct>> liveData = new MutableLiveData<>();
        Call<PagedModel<ServiceProduct>> call = serviceProductService.getServiceProducts(
                page, size, sortBy, sortDirection, name, description, type, categoryIds, available,
                visible, minPrice, maxPrice, availableEventTypeIds, serviceProductProviderId,
                minDuration, maxDuration, automaticReserved);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<PagedModel<ServiceProductSummaryDto>> getServiceProductSummaries(
            Integer page, Integer size, String sortBy, SortDirection sortDirection, String name,
            String description, ServiceProductDType type, List<Long> categoryIds, Boolean available,
            Boolean visible, Integer minPrice, Integer maxPrice, List<Long> availableEventTypeIds,
            Long serviceProductProviderId, Float minDuration, Float maxDuration, Boolean automaticReserved) {

        MutableLiveData<PagedModel<ServiceProductSummaryDto>> liveData = new MutableLiveData<>();
        Call<PagedModel<ServiceProductSummaryDto>> call = serviceProductService.getServiceProductSummaries(
                page, size, sortBy, sortDirection, name, description, type, categoryIds, available,
                visible, minPrice, maxPrice, availableEventTypeIds, serviceProductProviderId,
                minDuration, maxDuration, automaticReserved);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProduct> getServiceProductById(Long id) {
        MutableLiveData<ServiceProduct> liveData = new MutableLiveData<>();
        Call<ServiceProduct> call = serviceProductService.getServiceProductById(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProduct> createServiceProduct(ServiceProductDto serviceProductDto) {
        MutableLiveData<ServiceProduct> liveData = new MutableLiveData<>();
        Call<ServiceProduct> call = serviceProductService.createServiceProduct(serviceProductDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProduct> updateServiceProduct(Long id, ServiceProductDto serviceProductDto) {
        MutableLiveData<ServiceProduct> liveData = new MutableLiveData<>();
        Call<ServiceProduct> call = serviceProductService.updateServiceProduct(id, serviceProductDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteServiceProduct(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = serviceProductService.deleteServiceProduct(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<List<ServiceProductSummaryDto>> getTop5() {
        MutableLiveData<List<ServiceProductSummaryDto>> liveData = new MutableLiveData<>();
        Call<List<ServiceProductSummaryDto>> call = serviceProductService.getTop5();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProductFilteringValuesDto> getFilteringValues() {
        MutableLiveData<ServiceProductFilteringValuesDto> liveData = new MutableLiveData<>();
        Call<ServiceProductFilteringValuesDto> call = serviceProductService.getFilteringValues();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}