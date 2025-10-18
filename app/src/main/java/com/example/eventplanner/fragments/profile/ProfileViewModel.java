package com.example.eventplanner.fragments.profile;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.EventPlannerApp;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.repositories.serviceproduct.ImageRepository;
import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.dto.user.CompanyInfoDto;
import com.example.eventplanner.dto.user.UserDto;
import com.example.eventplanner.dto.user.UserInfoDto;
import com.example.eventplanner.utils.JsonLog;
import com.example.eventplanner.utils.ObserverTracker;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import lombok.Getter;

public class ProfileViewModel extends ViewModel {

   private final ProfileRepository profileRepository = new ProfileRepository();
   private final ImageRepository imageRepository = new ImageRepository();

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

   @Getter
   private final MutableLiveData<Integer> updateMessageResource = new MutableLiveData<>();
   @Getter
   private final MutableLiveData<String> updateMessageText = new MutableLiveData<>();
   //Favorite results
   private final MutableLiveData<Boolean> favoriteActionSuccess = new MutableLiveData<>();
   private ObserverTracker tracker = new ObserverTracker();
   public LiveData<Boolean> getFavoriteActionSuccess() { return favoriteActionSuccess; }

   public void resetMessages() {
      updateMessageResource.setValue(null);
      updateSuccess.setValue(null);
   }

   // ---- Loaders ----
   public void loadUser(long userId) {
      LiveData<UserDto> source = profileRepository.getUserData(userId);
      tracker.observeOnce(source, u -> {
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
      tracker.observeOnce(profileRepository.getFavoriteEvents(userId), favoriteEvents::setValue);
   }

   public void loadFavoriteServiceProducts(long userId) {
      tracker.observeOnce(profileRepository.getFavoriteServiceProducts(userId), favoriteServiceProducts::setValue);
   }

   // ---- Actions ----
   public void updatePersonalInfo(long userId, UserInfoDto info) {
      tracker.observeOnce(profileRepository.updatePersonalInfo(userId, info), updatedUser -> {
         boolean success = updatedUser != null;
         updateSuccess.setValue(success);
         if (success) {
            user.setValue(updatedUser);
         }
      });
   }

   public void updateCompanyInfo(long userId, CompanyInfoDto companyInfo) {
      tracker.observeOnce(profileRepository.updateCompanyInfo(userId, companyInfo), updatedCompany -> {
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
      tracker.observeOnce(profileRepository.changePassword(userId, oldPassword, newPassword), success -> {
         updateSuccess.setValue(Boolean.TRUE.equals(success));
      });
   }

   public void muteNotifications(long userId) {
      tracker.observeOnce(profileRepository.muteNotifications(userId), success -> {
         updateMessageResource.setValue(success
                 ? R.string.you_will_no_longer_receive_notifications
                 : R.string.failed_to_mute_notifications);
         if (success && user.getValue() != null) {
            UserDto current = user.getValue();
            current.setMutedNotifications(true);
            user.setValue(current);
         }
      });
   }

   public void unmuteNotifications(long userId) {
      tracker.observeOnce(profileRepository.unmuteNotifications(userId), success -> {
         updateMessageResource.setValue(success
               ? R.string.you_will_receive_notifications_again
               : R.string.failed_to_unmute_notifications);
         if (success && user.getValue() != null) {
            UserDto current = user.getValue();
            current.setMutedNotifications(false);
            user.setValue(current);
         }
      });
   }

   public void deleteAccount(long userId) {
      profileRepository.deleteAccount(userId).observeForever(success -> {
         updateSuccess.setValue(Boolean.TRUE.equals(success));
      });
   }

   // Favorite events
   public void addFavoriteEvent(long userId, long eventId) {
      tracker.observeOnce(profileRepository.addFavoriteEvent(userId, eventId), favoriteActionSuccess::setValue);
   }

   public void removeFavoriteEvent(long userId, long eventId) {
      tracker.observeOnce(profileRepository.removeFavoriteEvent(userId, eventId), favoriteActionSuccess::setValue);
   }

   // Favorite service products
   public void addFavoriteServiceProduct(long userId, long productId) {
      tracker.observeOnce(profileRepository.addFavoriteServiceProduct(userId, productId), favoriteActionSuccess::setValue);
   }

   public void removeFavoriteServiceProduct(long userId, long productId) {
      tracker.observeOnce(profileRepository.removeFavoriteServiceProduct(userId, productId), favoriteActionSuccess::setValue);
   }

   public void uploadProfilePicture(long userId, Context context, Uri imageUri) {
      tracker.observeOnce(imageRepository.uploadImage(context, imageUri), result -> {
         if (result == null) return;
         if (result.first != null) {
            byte[] nameBytes = Base64.decode(result.first, Base64.DEFAULT);
            String name = new String(nameBytes);
            tracker.observeOnce(profileRepository.uploadProfilePicture(userId, name), success -> {
               updateSuccess.setValue(success != null);
            });
         }
         else if (result.second != null)
            updateMessageText.setValue(result.second);
      });
   }

   public void removeProfilePicture(long userId) {
      tracker.observeOnce(profileRepository.removeProfilePicture(userId), success -> {
         updateSuccess.setValue(success != null);
      });
   }

   @Override
   protected void onCleared() {
      super.onCleared();
      tracker.clear();
   }
}
