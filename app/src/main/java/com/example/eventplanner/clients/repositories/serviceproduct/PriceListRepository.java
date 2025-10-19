package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.PriceListService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.PriceListDto;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class PriceListRepository {
    private final PriceListService priceListService;
    public PriceListRepository() { this.priceListService = ClientUtils.priceListService; }

    public LiveData<List<PriceListDto>> getMyProducts(long sppId) {
        MutableLiveData<List<PriceListDto>> liveData = new MutableLiveData<>();
        priceListService.getBySppId(sppId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }

    public LiveData<PriceListDto> updateProduct(Long id, double price, double discount) {
        MutableLiveData<PriceListDto> liveData = new MutableLiveData<>();
        priceListService.update(id, price, discount).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }


}
