package com.example.eventplanner.clients.repositories.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.order.BookingService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.order.BookingDto;
import com.example.eventplanner.dto.order.PendingBookingDto;
import com.example.eventplanner.model.order.Booking;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class BookingRepository {

    private final BookingService bookingService;

    public BookingRepository() {
        this.bookingService = ClientUtils.bookingService;
    }

    public LiveData<List<Booking>> getBookings() {
        MutableLiveData<List<Booking>> liveData = new MutableLiveData<>();
        Call<List<Booking>> call = bookingService.getBookings();

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Booking> getBookingById(Long id) {
        MutableLiveData<Booking> liveData = new MutableLiveData<>();
        Call<Booking> call = bookingService.getBookingById(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Booking> createBooking(BookingDto bookingDto) {
        MutableLiveData<Booking> liveData = new MutableLiveData<>();
        Call<Booking> call = bookingService.createBooking(bookingDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Booking> updateBooking(Long id, BookingDto bookingDto) {
        MutableLiveData<Booking> liveData = new MutableLiveData<>();
        Call<Booking> call = bookingService.updateBooking(id, bookingDto);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> deleteBooking(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<ResponseBody> call = bookingService.deleteBooking(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<PagedModel<PendingBookingDto>> getMyBookings(Integer page, Integer size) {
        MutableLiveData<PagedModel<PendingBookingDto>> liveData = new MutableLiveData<>();
        Call<PagedModel<PendingBookingDto>> call = bookingService.getMyBookings(page, size);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }

    public LiveData<Boolean> acceptBooking(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<Void> call = bookingService.acceptBooking(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(true),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }

    public LiveData<Boolean> declineBooking(Long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Call<Boolean> call = bookingService.declineBooking(id);

        call.enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
}
