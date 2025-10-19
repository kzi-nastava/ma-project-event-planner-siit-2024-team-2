package com.example.eventplanner.dto.serviceproduct;

import android.os.Build;

import com.example.eventplanner.model.serviceproduct.Service;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto extends ServiceProductDto implements Serializable {
    private String specifies;
    private float duration;
    private float minEngagementDuration;
    private float maxEngagementDuration;
    private int reservationDaysDeadline;
    private int cancellationDaysDeadline;
    private boolean hasAutomaticReservation;

    public ServiceDto(
            long categoryId,
            boolean available,
            boolean visible,
            double price,
            double discount,
            String name,
            String description,
            List<Long> availableEventTypeIds,
            Long serviceProductProviderId,
            String specifies,
            float duration,
            int reservationDaysDeadline,
            int cancellationDaysDeadline,
            boolean hasAutomaticReservation,
            List<String> images
    ) {
        this.categoryId = categoryId;
        this.available = available;
        this.visible = visible;
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.description = description;
        this.availableEventTypeIds = availableEventTypeIds;
        this.serviceProductProviderId = serviceProductProviderId;

        this.specifies = specifies;
        this.duration = duration;
        this.reservationDaysDeadline = reservationDaysDeadline;
        this.cancellationDaysDeadline = cancellationDaysDeadline;
        this.hasAutomaticReservation = hasAutomaticReservation;
        this.images = images;
    }}
