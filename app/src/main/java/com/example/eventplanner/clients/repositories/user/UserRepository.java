package com.example.eventplanner.clients.repositories.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.user.UserService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

   private final UserService userService;

   public UserRepository() {
      this.userService = ClientUtils.userService;
   }

   public LiveData<Void> suspendUser(String email) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.suspendUser(email).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<List<EventSummaryDto>> getFavoriteEvents(long userId) {
      MutableLiveData<List<EventSummaryDto>> liveData = new MutableLiveData<>();
      userService.getFavoriteEvents(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<Void> addFavoriteEvent(long userId, long eventId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.addFavoriteEvent(userId, eventId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> removeFavoriteEvent(long userId, long eventId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.removeFavoriteEvent(userId, eventId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<List<ServiceProductSummaryDto>> getFavoriteServiceProducts(long userId) {
      MutableLiveData<List<ServiceProductSummaryDto>> liveData = new MutableLiveData<>();
      userService.getFavoriteServiceProducts(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
              error -> liveData.setValue(new ArrayList<>())
      ));
      return liveData;
   }

   public LiveData<Void> addFavoriteServiceProduct(long userId, long serviceProductId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.addFavoriteServiceProduct(userId, serviceProductId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> removeFavoriteServiceProduct(long userId, long serviceProductId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.removeFavoriteServiceProduct(userId, serviceProductId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> blockUser(long userId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.blockUser(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> unblockUser(long userId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      userService.unblockUser(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }
}
