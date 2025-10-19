package com.example.eventplanner.dto.order;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long serviceId;
    private double price;
    private Instant date;
    private double duration;
}
