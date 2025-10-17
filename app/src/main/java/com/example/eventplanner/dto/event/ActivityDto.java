package com.example.eventplanner.dto.event;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto implements Serializable {
    private Long id;
    private String name;
    private Long activityStart;
    private Long activityEnd;
    private String description;
    private String location;
}
