package com.example.eventplanner.dto.serviceproduct;

import com.example.eventplanner.dto.event.EventTypeDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductFilteringValuesDto {
    private Double minPrice;
    private Double maxPrice;
    private Float minDuration;
    private Float maxDuration;
    private List<ServiceProductCategory> categories;
    private List<EventType> availableEventTypes;
}
