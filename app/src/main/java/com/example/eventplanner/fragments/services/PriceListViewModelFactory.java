package com.example.eventplanner.fragments.services;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.clients.repositories.serviceproduct.PriceListRepository;

import io.reactivex.annotations.NonNull;

public class PriceListViewModelFactory implements ViewModelProvider.Factory {

    private final PriceListRepository repository;

    public PriceListViewModelFactory(PriceListRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PriceListViewModel.class)) {
            return (T) new PriceListViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
