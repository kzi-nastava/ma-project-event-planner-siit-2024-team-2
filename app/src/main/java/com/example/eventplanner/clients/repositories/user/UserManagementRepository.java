package com.example.eventplanner.clients.repositories.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.utils.SimpleCallback;

import retrofit2.Call;

public class UserManagementRepository {

    public LiveData<Boolean> suspendUser(String email) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        
        Call<Void> call = ClientUtils.userService.suspendUser(email);
        
        call.enqueue(new SimpleCallback<>(
            response -> liveData.setValue(true),
            error -> liveData.setValue(false)
        ));
        
        return liveData;
    }
}
