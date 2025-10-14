package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ServiceProductCategoryService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceProductCategoryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ServiceProductCategoryRepository {

   private final ServiceProductCategoryService categoryService;

   public ServiceProductCategoryRepository() {
      this.categoryService = ClientUtils.serviceProductCategoryService;
   }

   public LiveData<List<ServiceProductCategory>> getAllServiceProductCategories() {
      MutableLiveData<List<ServiceProductCategory>> liveData = new MutableLiveData<>();
      categoryService.getAllServiceProductCategories().enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<ServiceProductCategory> getServiceProductCategoryById(Long id) {
      MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
      categoryService.getServiceProductCategoryById(id).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<ServiceProductCategory> createServiceProductCategory(ServiceProductCategoryDto dto) {
      MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
      categoryService.createServiceProductCategory(dto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<ServiceProductCategory> updateServiceProductCategory(Long id, ServiceProductCategoryDto dto) {
      MutableLiveData<ServiceProductCategory> liveData = new MutableLiveData<>();
      categoryService.updateServiceProductCategory(id, dto).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Boolean> deleteServiceProductCategory(Long id) {
      MutableLiveData<Boolean> liveData = new MutableLiveData<>();
      categoryService.deleteServiceProductCategory(id).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(true),
              error -> liveData.setValue(false)
      ));
      return liveData;
   }
}
