package com.example.eventplanner.fragments.order;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.order.BookingRepository;
import com.example.eventplanner.dto.order.PendingBookingDto;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import lombok.Getter;
import lombok.Setter;

public class ProviderBookingsViewModel extends ViewModel {
    private final BookingRepository bookingRepository = new BookingRepository();

    @Getter
    private final MutableLiveData<PagedModel<PendingBookingDto>> bookings = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> bookingAccepted = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> bookingDeclined = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();
    
    @Setter
    @Getter
    private int currentPage = 1;

    public void fetchBookings(Integer page, Integer size) {
        tracker.observeOnce(bookingRepository.getMyBookings(page, size), bookings, true);
    }

    public void acceptBooking(Long id) {
        tracker.observeOnce(bookingRepository.acceptBooking(id),
                success -> bookingAccepted.setValue(new Pair<>(id, success)));
    }

    public void declineBooking(Long id) {
        tracker.observeOnce(bookingRepository.declineBooking(id),
                success -> bookingDeclined.setValue(new Pair<>(id, success)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
