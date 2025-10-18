package com.example.eventplanner.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
   private long id;
   private String email;
   private String firstName;
   private String lastName;
   private String phoneNumber;
   private String address;
   private String imageEncodedName;
   private boolean isCompany;

   // For company users
   private String companyName;
   private String companyDescription;
   private String companyAddress;

   private boolean mutedNotifications = false;
}
