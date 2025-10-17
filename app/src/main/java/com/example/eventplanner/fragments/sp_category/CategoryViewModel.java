package com.example.eventplanner.fragments.sp_category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductCategoryRepository;
import com.example.eventplanner.dto.serviceproduct.ServiceProductCategoryDto;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final ServiceProductCategoryRepository categoryRepository;
    private final MediatorLiveData<List<ServiceProductCategory>> categoriesLiveData = new MediatorLiveData<>();

    public CategoryViewModel() {
        this.categoryRepository = new ServiceProductCategoryRepository();
    }

    public void loadCategories() {
        LiveData<List<ServiceProductCategory>> repoLiveData = categoryRepository.getAllServiceProductCategories();
        categoriesLiveData.addSource(repoLiveData, categories -> {
            categoriesLiveData.setValue(categories);
            categoriesLiveData.removeSource(repoLiveData);
        });
    }


    public LiveData<List<ServiceProductCategory>> getCategories() {
        return categoriesLiveData;
    }

    public LiveData<ServiceProductCategory> getCategoryById(Long id) {
        return categoryRepository.getServiceProductCategoryById(id);
    }

    public LiveData<ServiceProductCategory> createCategory(ServiceProductCategoryDto dto) {
        return categoryRepository.createServiceProductCategory(dto);
    }

    public LiveData<ServiceProductCategory> updateCategory(Long id, ServiceProductCategoryDto dto) {
        return categoryRepository.updateServiceProductCategory(id, dto);
    }

    public LiveData<Boolean> deleteCategory(Long id) {
        return categoryRepository.deleteServiceProductCategory(id);
    }
}