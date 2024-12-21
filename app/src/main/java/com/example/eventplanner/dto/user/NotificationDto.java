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
public class NotificationDto {
    private String message;
    private Date dateSent;
    private boolean seen;
    private long userId;
}
