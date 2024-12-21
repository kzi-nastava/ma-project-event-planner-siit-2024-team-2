package com.example.eventplanner.dto.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDto {
    private long reporterId;
    private long reportedId;
    private Date dateApproved;
    private String reason;
}
