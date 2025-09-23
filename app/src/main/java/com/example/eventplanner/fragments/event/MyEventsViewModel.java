package com.example.eventplanner.fragments.event;

import androidx.lifecycle.ViewModel;

import lombok.Getter;
import lombok.Setter;

public class MyEventsViewModel extends ViewModel {
   @Getter
   @Setter
   private int currentPage = 1;
}