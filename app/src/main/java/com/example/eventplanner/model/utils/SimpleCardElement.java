package com.example.eventplanner.model.utils;

import androidx.lifecycle.MutableLiveData;

import lombok.Getter;
import lombok.Setter;

public abstract class SimpleCardElement {
    abstract public String getTitle();
    abstract public String getSubtitle();
    abstract public String getBody();
    @Getter
    @Setter
    private boolean hidden;
}
