package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ServiceProductCategoryService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceProductCategoryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ServiceProductCategoryRepository {
    private final ServiceProductCategoryService serviceProductCategoryService;

    public ServiceProductCategoryRepository() {
        this.serviceProductCategoryService = ClientUtils.serviceProductCategoryService;
    }

    public LiveData<List<ServiceProductCategory>> getAllServiceProductCategories() {
        MutableLiveData<List<ServiceProductCategory>> liveData = new MutableLiveData<>();
        Call<List<ServiceProductCategory>> call = serviceProductCategoryService.getAllServiceProductCategories();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProductCategory> getServiceProductCategoryById(Long id) {
        MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
        Call<ServiceProductCategory> call = serviceProductCategoryService.getServiceProductCategoryById(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProductCategory> createServiceProductCategory(ServiceProductCategoryDto serviceProductCategoryDto) {
        MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
        Call<ServiceProductCategory> call = serviceProductCategoryService.createServiceProductCategory(serviceProductCategoryDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ServiceProductCategory> updateServiceProductCategory(Long id, ServiceProductCategoryDto serviceProductCategoryDto) {
        MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
        Call<ServiceProductCategory> call = serviceProductCategoryService.updateServiceProductCategory(id, serviceProductCategoryDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteServiceProductCategory(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = serviceProductCategoryService.deleteServiceProductCategory(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}