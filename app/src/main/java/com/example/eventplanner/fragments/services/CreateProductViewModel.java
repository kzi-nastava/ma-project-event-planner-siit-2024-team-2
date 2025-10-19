package com.example.eventplanner.fragments.services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ProductRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductCategoryRepository;
import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateProductViewModel extends ViewModel {
   private final ServiceProductCategoryRepository categoryRepository = new ServiceProductCategoryRepository();
   private final EventTypeRepository eventTypeRepository = new EventTypeRepository();
   private final ProductRepository productRepository = new ProductRepository();

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


   public void createProduct(ProductDto dto) {
      productRepository.createProduct(dto).observeForever(result -> {
         if (result != null) {
            success.setValue(true);
         } else {
            errorMessage.setValue("Failed to create product!");
         }
      });
   }

   public void updateProduct(ProductDto dto) {
      productRepository.updateProduct(dto.getId(), dto).observeForever(success -> {
         if (success != null) {
            this.success.postValue(true);
         } else {
            this.errorMessage.postValue("Failed to update product");
         }
      });
   }

}

