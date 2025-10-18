package com.example.eventplanner.fragments.admin;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.user.UserReportRepository;
import com.example.eventplanner.model.user.UserReport;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

public class AdminUserReportsViewModel extends ViewModel {
    private final UserReportRepository userReportRepository = new UserReportRepository();

    @Getter
    private final MutableLiveData<PagedModel<UserReport>> userReports = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> reportApproved = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> reportDeleted = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();
    @Setter
    @Getter
    private int currentPage = 1;

    public void fetchUserReports(Integer page, Integer size) {
        tracker.observeOnce(userReportRepository.getAllNotApproved(page, size), userReports, true);
    }

    public void approveReport(Long id) {
        tracker.observeOnce(userReportRepository.approve(id),
                success -> reportApproved.setValue(new Pair<>(id, success)));
    }

    public void deleteReport(Long id) {
        tracker.observeOnce(userReportRepository.delete(id),
                success -> reportDeleted.setValue(new Pair<>(id, success)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
