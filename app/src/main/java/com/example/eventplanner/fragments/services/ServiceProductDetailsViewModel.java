package com.example.eventplanner.fragments.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class ServiceProductDetailsViewModel extends ViewModel {

   private final MutableLiveData<ServiceProduct> serviceProduct = new MutableLiveData<>();

   public LiveData<ServiceProduct> getEventById(long eventId) {
      Call<ServiceProduct> call = ClientUtils.serviceProductService.getServiceProductById(eventId);
      call.enqueue(new SimpleCallback<>(
              response -> {
                 if (response != null) {
                    serviceProduct.setValue(response.body());
                 }
              },
              error -> serviceProduct.setValue(null)
      ));
      return serviceProduct;
   }
}