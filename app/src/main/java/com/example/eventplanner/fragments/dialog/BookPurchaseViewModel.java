package com.example.eventplanner.fragments.dialog;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.event.EventRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;
import com.example.eventplanner.dto.event.DateRangeDto;
import com.example.eventplanner.dto.order.BookingDto;
import com.example.eventplanner.dto.order.PurchaseDto;
import com.example.eventplanner.model.event.Budget;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class BookPurchaseViewModel extends ViewModel {
    private final EventRepository eventRepository = new EventRepository();
    private final ServiceRepository serviceRepository = new ServiceRepository();
    private final ObserverTracker tracker = new ObserverTracker();

    @Getter
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<List<DateRangeDto>> availableDates = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Boolean> bookingSuccess = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Boolean> purchaseSuccess = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Setter
    @Getter
    private ServiceProduct serviceProduct;
    @Setter
    @Getter
    private int selectedEventIndex = -1;
    @Setter
    @Getter
    private Long selectedBudgetId = -1L;
    @Setter
    @Getter
    private LocalDate selectedDate;
    @Setter
    @Getter
    private LocalTime selectedTime;
    @Setter
    @Getter
    private double selectedDuration;
    @Setter
    @Getter
    private double totalPrice;

    public void fetchEvents() {
        tracker.observeOnce(eventRepository.getAllMine(0, -1), result -> {
            if (result != null)
                events.setValue(result.getContent());
            else
                events.setValue(null);
        });
    }

    public void fetchBudgetsForEvent(int eventIndex) {
        List<Event> eventList = events.getValue();
        if (eventList != null && eventIndex >= 0 && eventIndex < eventList.size()) {
            Event event = eventList.get(eventIndex);
            List<Budget> filteredBudgets = new ArrayList<>();
            
            if (serviceProduct != null && serviceProduct.getCategory() != null) {
                for (Budget budget : event.getBudgets()) {
                    if (budget.getServiceProductCategory() != null && 
                        budget.getServiceProductCategory().getId().equals(serviceProduct.getCategory().getId())) {
                        filteredBudgets.add(budget);
                    }
                }
            }
            
            budgets.setValue(filteredBudgets);
        }
    }

    public void fetchAvailability(long serviceId, long eventId) {
        tracker.observeOnce(serviceRepository.getAvailability(serviceId, eventId), availableDates, true);
    }

    public void createBooking(long budgetId) {
        if (serviceProduct == null || selectedDate == null || selectedTime == null) {
            errorMessage.setValue("Missing required booking information");
            return;
        }

        // Combine date and time into Instant
        ZonedDateTime dateTime = selectedDate.atTime(selectedTime).atZone(ZoneId.systemDefault());
        Instant instant = dateTime.toInstant();

        BookingDto bookingDto = new BookingDto(
            serviceProduct.getId(),
            totalPrice,
            instant,
            selectedDuration
        );

        tracker.observeOnce(serviceRepository.createBooking(budgetId, bookingDto),
                result -> {
                    if (result.first != null)
                        bookingSuccess.setValue(result.first);
                    else {
                        bookingSuccess.setValue(false);
                        errorMessage.setValue(result.second);
                    }
                });
    }

    public void createPurchase(long budgetId) {
        if (serviceProduct == null) {
            errorMessage.setValue("Missing service product information");
            return;
        }

        PurchaseDto purchaseDto = new PurchaseDto(
            serviceProduct.getId(),
            totalPrice
        );

        tracker.observeOnce(serviceRepository.createPurchase(budgetId, purchaseDto),
                result -> {
                    if (result.first != null)
                        purchaseSuccess.setValue(result.first);
                    else {
                        purchaseSuccess.setValue(false);
                        errorMessage.setValue(result.second);
                    }
                });
    }

    public boolean isProduct() {
        return serviceProduct != null && "Product".equals(serviceProduct.getDtype());
    }

    public boolean hasFixedDuration() {
        if (serviceProduct instanceof Service) {
            Service service = (Service) serviceProduct;
            return service.getDuration() > 0;
        }
        return false;
    }

    public double getFixedDuration() {
        if (serviceProduct instanceof Service) {
            Service service = (Service) serviceProduct;
            return service.getDuration();
        }
        return 0;
    }

    public double getMinDuration() {
        if (serviceProduct instanceof Service) {
            Service service = (Service) serviceProduct;
            return service.getMinEngagementDuration();
        }
        return 0;
    }

    public double getMaxDuration() {
        if (serviceProduct instanceof Service) {
            Service service = (Service) serviceProduct;
            return service.getMaxEngagementDuration();
        }
        return 24;
    }

    public boolean isAutomaticReservation() {
        if (serviceProduct instanceof Service) {
            Service service = (Service) serviceProduct;
            return service.isHasAutomaticReservation();
        }
        return false;
    }

    public String getEventName() {
        List<Event> eventList = events.getValue();
        if (eventList != null && selectedEventIndex >= 0 && selectedEventIndex < eventList.size()) {
            return eventList.get(selectedEventIndex).getName();
        }
        return "Not selected";
    }

    public boolean canConfirm() {
        if (selectedEventIndex == -1) {
            return false;
        }

        if (selectedBudgetId == null || selectedBudgetId == -1) {
            return false;
        }
        
        if (isProduct()) {
            return true;
        }
        
        return selectedDate != null && selectedTime != null && selectedDuration > 0;
    }

    public List<DateRangeDto> getTimeWindowsForSelectedDate() {
        if (selectedDate == null || selectedDuration <= 0) {
            return new ArrayList<>();
        }

        List<DateRangeDto> timeWindows = new ArrayList<>();
        List<DateRangeDto> availableDatesList = availableDates.getValue();
        
        if (availableDatesList == null) {
            return timeWindows;
        }

        long dayStart = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long dayEnd = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long durationMs = (long) (selectedDuration * 60 * 60 * 1000);

        for (DateRangeDto dateRange : availableDatesList) {
            long start = Math.max(dateRange.getStart(), dayStart);
            long end = Math.min(dateRange.getEnd(), dayEnd);
            
            if (start < end && (end - start) >= durationMs) {
                timeWindows.add(new DateRangeDto(start, end));
            }
        }

        return timeWindows;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
