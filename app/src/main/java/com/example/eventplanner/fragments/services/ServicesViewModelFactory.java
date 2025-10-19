package com.example.eventplanner.fragments.services;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;

public class ServicesViewModelFactory implements ViewModelProvider.Factory {
    private final ServiceRepository repository;

    public ServicesViewModelFactory(ServiceRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ServicesViewModel.class)) {
            return (T) new ServicesViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

