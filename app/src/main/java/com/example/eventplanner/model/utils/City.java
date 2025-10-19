package com.example.eventplanner.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City implements Comparable<City>{
    private String city;
    private double lat, lng;

    @Override
    public int compareTo(City c) {
        if (c == null)
            return 1;
        return city.compareTo(c.city);
    }
}
