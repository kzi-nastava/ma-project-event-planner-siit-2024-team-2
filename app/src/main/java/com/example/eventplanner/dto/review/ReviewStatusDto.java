package com.example.eventplanner.dto.review;

import com.example.eventplanner.model.review.ReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatusDto {
    private long id;
    private ReviewStatus reviewStatus;
}
