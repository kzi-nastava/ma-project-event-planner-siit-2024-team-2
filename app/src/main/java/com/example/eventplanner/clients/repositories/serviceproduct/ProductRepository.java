package com.example.eventplanner.clients.repositories.serviceproduct;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.serviceproduct.ProductService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    private final ProductService productService;

    public ProductRepository() {
        this.productService = ClientUtils.productService;
    }

    public LiveData<List<ProductDto>> getMyProducts() {
        MutableLiveData<List<ProductDto>> liveData = new MutableLiveData<>();
        productService.getMyProducts().enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }

    public LiveData<ProductDto> createProduct(ProductDto dto) {
        MutableLiveData<ProductDto> liveData = new MutableLiveData<>();
        productService.createProduct(dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<ProductDto> updateProduct(Long id, ProductDto dto) {
        MutableLiveData<ProductDto> liveData = new MutableLiveData<>();
        productService.updateProduct(id, dto).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteProduct(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        productService.deleteProduct(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(true),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}
