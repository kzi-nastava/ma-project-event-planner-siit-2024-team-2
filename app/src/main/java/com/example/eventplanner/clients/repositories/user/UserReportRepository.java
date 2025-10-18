package com.example.eventplanner.clients.repositories.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.user.UserReportDto;
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
}
