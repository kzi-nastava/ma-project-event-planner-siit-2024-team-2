package com.example.eventplanner.dto.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {
    private double plannedSpending;
    private double maxAmount;
    private long serviceProductCategoryId;
}
