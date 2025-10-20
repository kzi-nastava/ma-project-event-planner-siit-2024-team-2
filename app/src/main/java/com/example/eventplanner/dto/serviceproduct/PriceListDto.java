package com.example.eventplanner.dto.serviceproduct;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceListDto implements Serializable {
    long id;
    String name;
    double price;
    double discount;
}
