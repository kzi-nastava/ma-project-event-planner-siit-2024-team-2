package com.example.eventplanner.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageMetadata {
    Integer size;
    Integer number;
    Long totalElements;
    Integer totalPages;
}
