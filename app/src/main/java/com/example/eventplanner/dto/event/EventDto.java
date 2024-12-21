package com.example.eventplanner.dto.event;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String name;
    private String description;
    private long type;
    private int maxAttendances;
    private boolean open;
    private double longitude;
    private double latitude;
    private Date date;
    private List<Long> activityIds;
    private List<Long> budgetIds;
}
