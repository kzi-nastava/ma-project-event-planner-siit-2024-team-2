package com.example.eventplanner.dto.auth;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterServiceProductProviderDto extends RegisterUserDto{
    @NotNull
    private String companyName;
    @NotNull
    private String companyDescription;
}