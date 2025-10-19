package com.example.eventplanner.dto.serviceproduct;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@ToString
public class ProductDto extends ServiceProductDto implements Serializable {

    public ProductDto(long categoryId,
                      boolean available,
                      boolean visible,
                      double price,
                      double discount,
                      String name,
                      String description,
                      List<String> images,
                      List<Long> availableEventTypeIds,
                      long serviceProductProviderId) {
        this.categoryId = categoryId;
        this.available = available;
        this.visible = visible;
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.description = description;
        this.images = images;
        this.availableEventTypeIds = availableEventTypeIds;
        this.serviceProductProviderId = serviceProductProviderId;
    }
}
