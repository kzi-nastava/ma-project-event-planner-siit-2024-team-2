package com.example.eventplanner.fragments.communication;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.communication.NotificationRepository;
import com.example.eventplanner.model.communication.Notification;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

public class NotificationsViewModel extends ViewModel {
    private final NotificationRepository notificationRepository = new NotificationRepository();

    @Getter
    private final MutableLiveData<PagedModel<Notification>> notifications = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> notificationDismissed = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();
    @Setter
    @Getter
    private int currentPage = 1;

    public void fetchNotifications(Integer page, Integer size, String sentAt) {
        tracker.observeOnce(notificationRepository.getUserNotifications(page, size, sentAt), notifications, true);
    }

    public void dismissNotification(Long id) {
        tracker.observeOnce(notificationRepository.dismissNotifications(List.of(id)),
                success -> notificationDismissed.setValue(new Pair<>(id, success)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
