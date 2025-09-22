package com.example.eventplanner.dto.auth;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {
    private String oldPassword;
    @NotNull
    private String newPassword;
    private long userId;
}
