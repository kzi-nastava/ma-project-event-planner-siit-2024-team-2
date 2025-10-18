package com.example.eventplanner.clients.repositories.communication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.communication.NotificationService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.communication.NotificationDto;
import com.example.eventplanner.model.communication.Notification;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class NotificationRepository {

    private final NotificationService notificationService;

    public NotificationRepository() {
        this.notificationService = ClientUtils.notificationService;
    }

    public LiveData<PagedModel<Notification>> getNotifications(Integer page, Integer size) {
        MutableLiveData<PagedModel<Notification>> liveData = new MutableLiveData<>();
        Call<PagedModel<Notification>> call = notificationService.getNotifications(page, size);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Notification> getNotificationById(Long id) {
        MutableLiveData<Notification> liveData = new MutableLiveData<>();
        Call<Notification> call = notificationService.getNotificationById(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Notification> createNotification(NotificationDto dto) {
        MutableLiveData<Notification> liveData = new MutableLiveData<>();
        Call<Notification> call = notificationService.createNotification(dto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Notification> updateNotification(Long id, NotificationDto dto) {
        MutableLiveData<Notification> liveData = new MutableLiveData<>();
        Call<Notification> call = notificationService.updateNotification(id, dto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> dismissNotifications(List<Long> ids) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = notificationService.dismissNotifications(ids);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<Boolean> seenNotifications(List<Long> ids) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = notificationService.seenNotifications(ids);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteNotification(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = notificationService.deleteNotification(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<PagedModel<Notification>> getUserNotifications(Integer page, Integer size, String sentAt) {
        MutableLiveData<PagedModel<Notification>> liveData = new MutableLiveData<>();
        Call<PagedModel<Notification>> call = notificationService.getUserNotifications(page, size, sentAt);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> sendCategoryRequest(NotificationDto dto) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = notificationService.sendCategoryRequest(dto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null && response.isSuccessful()),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}