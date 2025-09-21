package com.example.eventplanner.dto.auth;

import com.example.eventplanner.model.utils.UserRole;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Long id;
    private String email;
    private String jwt;
    private UserRole role;
    private Instant suspendedAt = null;
}