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
    private long eventTypeId;
    private long eventOrganizerId;
    private int maxAttendances;
    private boolean open;
    private double longitude;
    private double latitude;
    private String date;
    private List<Long> activityIds;
    private List<Long> budgetIds;
    private List<String> invitationEmails;
}
