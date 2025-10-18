package com.example.eventplanner.clients.repositories.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.communication.InvitationService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.InvitationDto;
import com.example.eventplanner.model.event.Invitation;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import retrofit2.Call;

public class InvitationRepository {

    private final InvitationService invitationService;

    public InvitationRepository() {
        this.invitationService = ClientUtils.invitationService;
    }

    public LiveData<List<Invitation>> getAllInvitations() {
        MutableLiveData<List<Invitation>> liveData = new MutableLiveData<>();
        Call<List<Invitation>> call = invitationService.getAllInvitations();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Invitation> getInvitation(Long id) {
        MutableLiveData<Invitation> liveData = new MutableLiveData<>();
        Call<Invitation> call = invitationService.getInvitation(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Invitation> updateInvitation(Long id, InvitationDto dto) {
        MutableLiveData<Invitation> liveData = new MutableLiveData<>();
        Call<Invitation> call = invitationService.updateInvitation(id, dto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Invitation> acceptInvitation(String token) {
        MutableLiveData<Invitation> liveData = new MutableLiveData<>();
        Call<Invitation> call = invitationService.acceptInvitation(token);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}