package com.example.eventplanner.fragments.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.PriceListRepository;
import com.example.eventplanner.dto.serviceproduct.PriceListDto;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PriceListViewModel extends ViewModel {
    private final PriceListRepository repository;

    private final MutableLiveData<Boolean> success = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PriceListViewModel(PriceListRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<PriceListDto>> getPriceListBySppId(long sppId) {
        return repository.getBySppId(sppId);
    }

    public void updateItem(PriceListDto item) {
        repository.updateItem(item.getId(), item.getPrice(), item.getDiscount()).observeForever(success -> {
            if (success != null) {
                this.success.postValue(true);
            } else {
                this.errorMessage.postValue("Failed to update service");
            }
        });
    }

    public LiveData<ResponseBody> downloadPdf(long sppId) {
        MutableLiveData<ResponseBody> liveData = new MutableLiveData<>();
        repository.downloadPdf(sppId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}
