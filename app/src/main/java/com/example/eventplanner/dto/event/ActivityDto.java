package com.example.eventplanner.dto.event;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private String name;
    private Date activityStart;
    private Date activityEnd;
    private String description;
    private String location;
}
