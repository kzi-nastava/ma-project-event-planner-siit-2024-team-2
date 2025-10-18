package com.example.eventplanner.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {
    private long id;
    private Event eventDto;
    private String email;
    private String token;
    private boolean accepted;
    private boolean quickRegistration; // Whether the user uses the quick registration/login
    private boolean justRegistered; // Used to show a welcome message to new users
}