package com.example.eventplanner.dto.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventOrganizerDto extends BaseUserDto {
    private List<Long> favoriteServiceProductIds;
}
