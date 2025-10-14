package com.example.eventplanner.fragments.eventtype;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductCategoryRepository;
import com.example.eventplanner.dto.event.CreateEventTypeDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.List;

public class NewEventTypeViewModel extends ViewModel {

   private final ServiceProductCategoryRepository categoryRepository;
   private final EventTypeRepository eventTypeRepository;

   private final MutableLiveData<List<ServiceProductCategory>> categories = new MutableLiveData<>();

   public NewEventTypeViewModel() {
      categoryRepository = new ServiceProductCategoryRepository();
      eventTypeRepository = new EventTypeRepository();
      loadCategories(); // 👈 load immediately
   }

   // Fetch all service product categories
   private void loadCategories() {
      categoryRepository.getAllServiceProductCategories().observeForever(categories::setValue);
   }

   public LiveData<List<ServiceProductCategory>> getServiceProductCategories() {
      return categories;
   }

   // Submit new event type
   public LiveData<EventType> createEventType(CreateEventTypeDto dto) {
      return eventTypeRepository.createEventType(dto);
   }
}
