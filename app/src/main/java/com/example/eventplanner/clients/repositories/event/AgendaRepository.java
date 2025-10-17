package com.example.eventplanner.clients.repositories.event;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.event.AgendaService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.event.ActivityDto;
import com.example.eventplanner.model.event.Activity;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AgendaRepository {
    private final AgendaService AgendaService;

    public AgendaRepository() {
        this.AgendaService = ClientUtils.agendaService;
    }

    public LiveData<List<ActivityDto>> getAllAgendas(Long eventId) {
        MutableLiveData<List<ActivityDto>> liveData = new MutableLiveData<>();
        AgendaService.getAgenda(eventId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }

    public LiveData<ActivityDto> getAgendaById(Long id) {
        MutableLiveData<ActivityDto> liveData = new MutableLiveData<>();
        AgendaService.getAgenda(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? (ActivityDto) response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Activity> createAgenda(Long eventId, List<ActivityDto> dto) {
        MutableLiveData<Activity> liveData = new MutableLiveData<>();
        AgendaService.createAgenda(eventId, dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? (Activity) response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ActivityDto> updateAgenda(Long eventId, Long id, ActivityDto dto) {
        MutableLiveData<ActivityDto> liveData = new MutableLiveData<>();
        AgendaService.updateActivity(eventId, id, dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteAgenda(Long eventId, Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<Void> call = AgendaService.deleteActivity(eventId, id);
        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(true),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}
