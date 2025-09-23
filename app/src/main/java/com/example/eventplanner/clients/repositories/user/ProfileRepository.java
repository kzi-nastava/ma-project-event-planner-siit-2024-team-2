package com.example.eventplanner.clients.repositories.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.user.ProfileService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.dto.user.UserDto;
import com.example.eventplanner.dto.user.UserInfoDto;
import com.example.eventplanner.dto.user.CompanyInfoDto;
import com.example.eventplanner.clients.services.user.ProfileService.ImageNameBody;
import com.example.eventplanner.clients.services.user.ProfileService.PasswordBody;
import com.example.eventplanner.clients.services.user.ProfileService.EventTypesBody;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

public class ProfileRepository {

   private final ProfileService profileService;

   public ProfileRepository() {
      this.profileService = ClientUtils.profileService;
   }

   public LiveData<UserDto> getUserData(long userId) {
      MutableLiveData<UserDto> liveData = new MutableLiveData<>();
      profileService.getUserData(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<UserDto> uploadProfilePicture(long userId, String imageName) {
      MutableLiveData<UserDto> liveData = new MutableLiveData<>();
      profileService.uploadProfilePicture(userId, new ImageNameBody(imageName)).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<UserDto> removeProfilePicture(long userId) {
      MutableLiveData<UserDto> liveData = new MutableLiveData<>();
      profileService.removeProfilePicture(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<UserDto> updatePersonalInfo(long userId, UserInfoDto info) {
      MutableLiveData<UserDto> liveData = new MutableLiveData<>();
      profileService.updatePersonalInfo(userId, info).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> changePassword(long userId, String oldPassword, String newPassword) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      profileService.changePassword(userId, new PasswordBody(oldPassword, newPassword)).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> deleteAccount(long userId) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      profileService.delete(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<Void> updateEventTypes(List<String> selectedEventTypes) {
      MutableLiveData<Void> liveData = new MutableLiveData<>();
      profileService.updateEventTypes(new EventTypesBody(selectedEventTypes)).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<CompanyInfoDto> getCompanyData(long userId) {
      MutableLiveData<CompanyInfoDto> liveData = new MutableLiveData<>();
      profileService.getCompanyData(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<List<EventSummaryDto>> getFavoriteEvents(long userId) {
      MutableLiveData<List<EventSummaryDto>> liveData = new MutableLiveData<>();
      profileService.getFavoriteEvents(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<List<ServiceProductSummaryDto>> getFavoriteServiceProducts(long userId) {
      MutableLiveData<List<ServiceProductSummaryDto>> liveData = new MutableLiveData<>();
      profileService.getFavoriteServiceProducts(userId).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

   public LiveData<CompanyInfoDto> updateCompanyInfo(long userId, CompanyInfoDto companyInfo) {
      MutableLiveData<CompanyInfoDto> liveData = new MutableLiveData<>();
      profileService.updateCompanyInfo(userId, companyInfo).enqueue(new SimpleCallback<>(
              response -> liveData.setValue(response != null ? response.body() : null),
              error -> liveData.setValue(null)
      ));
      return liveData;
   }

}
