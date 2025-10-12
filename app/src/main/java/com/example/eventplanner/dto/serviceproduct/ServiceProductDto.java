package com.example.eventplanner.dto.serviceproduct;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductDto {
    protected long id;
	protected long categoryId;
    protected boolean available;
    protected boolean visible;
    protected double price;
    protected double discount;
    protected String name;
    protected String description;
    protected List<String> images;
    protected List<Long> availableEventTypeIds;
    protected Long serviceProductProviderId;
}
