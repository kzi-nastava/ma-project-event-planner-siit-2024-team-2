package com.example.eventplanner.dto.user;

import com.example.eventplanner.model.utils.UserRole;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserDto {
    protected String email;
    protected String password;
    protected UserRole userRole;
    protected String firstName;
    protected String lastName;
    protected String address;
    protected String phoneNumber;
    protected List<Long> blockedUserIds;
}
