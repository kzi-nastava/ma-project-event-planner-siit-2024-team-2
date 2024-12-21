package com.example.eventplanner.dto.order;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long eventId;
    private long serviceId;
    private double price;
    private Date date;
    private double duration;
}
