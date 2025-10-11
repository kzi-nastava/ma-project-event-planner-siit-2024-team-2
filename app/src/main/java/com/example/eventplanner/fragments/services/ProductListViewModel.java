package com.example.eventplanner.fragments.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.ProductRepository;
import com.example.eventplanner.dto.serviceproduct.ProductDto;

import java.util.List;

public class ProductListViewModel extends ViewModel {

    private final ProductRepository repository = new ProductRepository();
    private final MutableLiveData<List<ProductDto>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();

    public LiveData<List<ProductDto>> getProducts() { return products; }
    public LiveData<Boolean> getDeleteSuccess() { return deleteSuccess; }

    public void loadProducts() {
        repository.getMyProducts().observeForever(products::setValue);
    }

    public void deleteProduct(long productId) {
        repository.deleteProduct(productId).observeForever(deleteSuccess::setValue);
    }
}
