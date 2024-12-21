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
    Long size;
    Long number;
    Long totalElements;
    Long totalPages;
}
