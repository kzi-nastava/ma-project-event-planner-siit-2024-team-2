package com.example.eventplanner.model.utils;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedModel<E> {
    List<E> content;
    PageMetadata page;
}
