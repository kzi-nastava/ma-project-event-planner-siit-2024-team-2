package com.example.eventplanner.dto.order;

import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.serviceproduct.ProductDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
    private long eventId;
    private long productId;
    private double price;
}
