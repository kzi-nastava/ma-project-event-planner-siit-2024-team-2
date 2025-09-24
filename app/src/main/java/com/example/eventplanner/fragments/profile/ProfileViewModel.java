package com.example.eventplanner.fragments.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.dto.user.CompanyInfoDto;
import com.example.eventplanner.dto.user.UserDto;
import com.example.eventplanner.dto.user.UserInfoDto;

import java.util.List;

public class ProfileViewModel extends ViewModel {

   private final ProfileRepository profileRepository = new ProfileRepository();

   // User data
   private final MutableLiveData<UserDto> user = new MutableLiveData<>();
   public LiveData<UserDto> getUser() { return user; }

   // Favorites
   private final MutableLiveData<List<EventSummaryDto>> favoriteEvents = new MutableLiveData<>();
   public LiveData<List<EventSummaryDto>> getFavoriteEvents() { return favoriteEvents; }

   private final MutableLiveData<List<ServiceProductSummaryDto>> favoriteServiceProducts = new MutableLiveData<>();
   public LiveData<List<ServiceProductSummaryDto>> getFavoriteServiceProducts() { return favoriteServiceProducts; }

   // Update results
   private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();
   public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }
   //Favorite results
   private final MutableLiveData<Boolean> favoriteActionSuccess = new MutableLiveData<>();
   public LiveData<Boolean> getFavoriteActionSuccess() { return favoriteActionSuccess; }

   // ---- Loaders ----
   public void loadUser(long userId) {
      LiveData<UserDto> source = profileRepository.getUserData(userId);
      source.observeForever(u -> {
         if (u != null) {
            user.setValue(u);
         }
      });
   }

   public void loadCompanyData(long userId) {
      LiveData<CompanyInfoDto> source = profileRepository.getCompanyData(userId);
      source.observeForever(company -> {
         if (company != null && user.getValue() != null) {
            UserDto current = user.getValue();
            current.setCompanyName(company.getCompanyName());
            user.setValue(current);
         }
      });
   }

   public void loadFavoriteEvents(long userId) {
      profileRepository.getFavoriteEvents(userId).observeForever(favoriteEvents::setValue);
   }

   public void loadFavoriteServiceProducts(long userId) {
      profileRepository.getFavoriteServiceProducts(userId).observeForever(favoriteServiceProducts::setValue);
   }

   // ---- Actions ----
   public void updatePersonalInfo(long userId, UserInfoDto info) {
      profileRepository.updatePersonalInfo(userId, info).observeForever(updatedUser -> {
         boolean success = updatedUser != null;
         updateSuccess.setValue(success);
         if (success) {
            user.setValue(updatedUser);
         }
      });
   }

   public void updateCompanyInfo(long userId, CompanyInfoDto companyInfo) {
      profileRepository.updateCompanyInfo(userId, companyInfo).observeForever(updatedCompany -> {
         boolean success = updatedCompany != null;
         updateSuccess.setValue(success);
         if (success && user.getValue() != null) {
            UserDto current = user.getValue();
            current.setCompanyName(updatedCompany.getCompanyName());
            user.setValue(current);
         }
      });
   }

   public void changePassword(long userId, String oldPassword, String newPassword) {
      profileRepository.changePassword(userId, oldPassword, newPassword).observeForever(success -> {
         updateSuccess.setValue(Boolean.TRUE.equals(success));
      });
   }

   public void deleteAccount(long userId) {
      profileRepository.deleteAccount(userId).observeForever(success -> {
         updateSuccess.setValue(Boolean.TRUE.equals(success));
      });
   }

   // Favorite events
   public void addFavoriteEvent(long userId, long eventId) {
      profileRepository.addFavoriteEvent(userId, eventId).observeForever(favoriteActionSuccess::setValue);
   }

   public void removeFavoriteEvent(long userId, long eventId) {
      profileRepository.removeFavoriteEvent(userId, eventId).observeForever(favoriteActionSuccess::setValue);
   }

   // Favorite service products
   public void addFavoriteServiceProduct(long userId, long productId) {
      profileRepository.addFavoriteServiceProduct(userId, productId).observeForever(favoriteActionSuccess::setValue);
   }

   public void removeFavoriteServiceProduct(long userId, long productId) {
      profileRepository.removeFavoriteServiceProduct(userId, productId).observeForever(favoriteActionSuccess::setValue);
   }
}
