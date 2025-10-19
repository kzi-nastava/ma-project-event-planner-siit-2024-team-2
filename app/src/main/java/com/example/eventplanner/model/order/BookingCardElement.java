package com.example.eventplanner.model.order;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.example.eventplanner.dto.order.PendingBookingDto;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.utils.SimpleCardElement;

import java.time.Instant;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingCardElement extends SimpleCardElement {
    private Long id;
    private Service service;
    private double price;
    private Long date;
    private double duration;
    private Instant createdAt;
    private String bookerName;
    private String bookerEmail;
    private boolean hiding;

    @Override
    public String getTitle() {
        String bookerInfo = getBookerInfo();
        String serviceName = service != null ? service.getName() : "Unknown Service";
        
        return String.format("**%s** wants to book service **%s**", bookerInfo, serviceName);
    }

    @Override
    public String getSubtitle() {
        return createdAt != null ? DateUtils.getRelativeTimeSpanString(createdAt.toEpochMilli()).toString() : "";
    }

    @Override
    public String getBody() {
        StringBuilder body = new StringBuilder();
        
        if (date != null) {
            body.append("**Date:** ").append(DateFormat.format("dd.MM.yyyy", date)).append("\n");
            body.append("**Start time:** ").append(DateFormat.format("HH:mm", date)).append("\n");
            
            // Calculate end time
            long endTime = date + (long) (duration * 60 * 60 * 1000);
            String endTimeStr = DateFormat.format("HH:mm", new Date(endTime)).toString();
            if ("00:00".equals(endTimeStr)) {
                endTimeStr = "24:00";
            }
            body.append("**End time:** ").append(endTimeStr).append("\n");
        }
        
        body.append("**Price:** ").append(String.format("%.2f", price)).append(" €");
        
        return body.toString();
    }

    private String getBookerInfo() {
        if (bookerName != null && !bookerName.isEmpty()) {
            return bookerName + " (" + (bookerEmail != null ? bookerEmail : "") + ")";
        } else if (bookerEmail != null && !bookerEmail.isEmpty()) {
            return bookerEmail;
        } else {
            return "Deleted User";
        }
    }

    public static BookingCardElement fromPendingBookingDto(PendingBookingDto booking) {
        BookingCardElement element = new BookingCardElement();
        element.setId(booking.getId());
        element.setService(booking.getService());
        element.setPrice(booking.getPrice());
        element.setDate(booking.getDate());
        element.setDuration(booking.getDuration());
        element.setCreatedAt(booking.getCreatedAt());
        element.setBookerName(booking.getBookerName());
        element.setBookerEmail(booking.getBookerEmail());
        element.setHiding(booking.isHiding());
        element.setHidden(booking.isHidden());
        return element;
    }

    public Long getServiceId() {
        return service != null ? service.getId() : null;
    }
}
