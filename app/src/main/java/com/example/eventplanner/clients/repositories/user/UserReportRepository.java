package com.example.eventplanner.clients.repositories.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.user.UserReportDto;
import com.example.eventplanner.model.user.UserReport;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.SimpleCallback;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserReportRepository {

    public LiveData<Boolean> reportUser(String reportedEmail, String reason) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        
        UserReportDto reportDto = new UserReportDto(reportedEmail, reason);
        Call<ResponseBody> call = ClientUtils.userReportService.reportUser(reportDto);
        
        call.enqueue(new SimpleCallback<>(
            response -> liveData.setValue(true),
            error -> liveData.setValue(false)
        ));
        
        return liveData;
    }

    public LiveData<PagedModel<UserReport>> getAllNotApproved(Integer page, Integer size) {
        MutableLiveData<PagedModel<UserReport>> liveData = new MutableLiveData<>();
        
        Call<PagedModel<UserReport>> call = ClientUtils.userReportService.getAllNotApproved(page, size);
        
        call.enqueue(new SimpleCallback<>(
            response -> liveData.setValue(response != null ? response.body() : null),
            error -> liveData.setValue(null)
        ));
        
        return liveData;
    }

    public LiveData<Boolean> approve(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        
        Call<UserReport> call = ClientUtils.userReportService.approve(id);
        
        call.enqueue(new SimpleCallback<>(
            response -> liveData.setValue(true),
            error -> liveData.setValue(false)
        ));
        
        return liveData;
    }

    public LiveData<Boolean> delete(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        
        Call<Boolean> call = ClientUtils.userReportService.delete(id);
        
        call.enqueue(new SimpleCallback<>(
            response -> liveData.setValue(response != null ? response.body() : false),
            error -> liveData.setValue(false)
        ));
        
        return liveData;
    }
}
