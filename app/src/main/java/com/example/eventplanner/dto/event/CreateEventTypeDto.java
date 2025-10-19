package com.example.eventplanner.dto.event;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventTypeDto {
   public String name;
   public String description;
   public List<Long> recommendedServiceProducts = new ArrayList<>();
}
