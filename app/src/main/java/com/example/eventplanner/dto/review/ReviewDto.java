package com.example.eventplanner.dto.review;

import com.example.eventplanner.model.review.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private int grade;
    private String comment;
    private long entityId;
    private ReviewType reviewType;
}
