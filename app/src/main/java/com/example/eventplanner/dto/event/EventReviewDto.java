package com.example.eventplanner.dto.event;

import com.example.eventplanner.dto.user.BaseUserDto;
import com.example.eventplanner.model.utils.ReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventReviewDto {
    private int grade;
    private String comment;
    private BaseUserDto user;
    private EventDto eventId;
    private ReviewStatus reviewStatus;
}
