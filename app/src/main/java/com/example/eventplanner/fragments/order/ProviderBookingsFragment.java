package com.example.eventplanner.fragments.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.SimpleCardAdapter;
import com.example.eventplanner.databinding.FragmentProviderBookingsBinding;
import com.example.eventplanner.dto.order.PendingBookingDto;
import com.example.eventplanner.model.order.BookingCardElement;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;

public class ProviderBookingsFragment extends Fragment {
    private FragmentProviderBookingsBinding binding;
    private ProviderBookingsViewModel viewModel;
    private SimpleCardAdapter<BookingCardElement> adapter;
    private List<BookingCardElement> bookingCards = new ArrayList<>();

    // Pagination
    private Pagination pagination;
    private int currentPage = 1;
    private static final int pageSize = 10;
    public static PageMetadata pageMetadata;

    public ProviderBookingsFragment() {
        // Required empty public constructor
    }

    public static ProviderBookingsFragment newInstance() {
        return new ProviderBookingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProviderBookingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ProviderBookingsViewModel.class);

        pagination = new Pagination(getContext(), 0, binding.paginationBookings);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchBookings();
        });

        adapter = new SimpleCardAdapter<>(bookingCards,
            "Accept", this::acceptBooking,
            "Decline", this::declineBooking
        );
        RecyclerView recyclerView = binding.recyclerViewBookings;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getBookings().observe(getViewLifecycleOwner(), value -> {
            bookingCards.clear();
            if (value != null) {
                List<PendingBookingDto> bookings = value.getContent();
                // Convert PendingBookingDto to BookingCardElement
                for (PendingBookingDto booking : bookings) {
                    BookingCardElement cardElement = BookingCardElement.fromPendingBookingDto(booking);
                    bookingCards.add(cardElement);
                }
                int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                pageMetadata = value.getPage();
                if (previousTotalPages != pageMetadata.getTotalPages())
                    pagination.changeTotalPages(pageMetadata.getTotalPages());
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });

        viewModel.getBookingAccepted().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "Booking accepted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to accept booking", Toast.LENGTH_SHORT).show();
                restoreBookingInList(id);
            }
        });

        viewModel.getBookingDeclined().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "Booking declined successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to decline booking", Toast.LENGTH_SHORT).show();
                restoreBookingInList(id);
            }
        });

        fetchBookings();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding.paginationBookings.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationBookings);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchBookings();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

    private void acceptBooking(BookingCardElement element) {
        element.setHidden(true);
        adapter.notifyItemChanged(bookingCards.indexOf(element));
        viewModel.acceptBooking(element.getId());
    }

    private void declineBooking(BookingCardElement element) {
        element.setHidden(true);
        adapter.notifyItemChanged(bookingCards.indexOf(element));
        viewModel.declineBooking(element.getId());
    }

    private void restoreBookingInList(long bookingId) {
        for (BookingCardElement element : bookingCards) {
            if (element.getId().equals(bookingId)) {
                element.setHidden(false);
                adapter.notifyItemChanged(bookingCards.indexOf(element));
                break;
            }
        }
    }

    private void updateEmptyState() {
        TextView emptyText = binding.textEmptyBookings;
        if (bookingCards.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    private void fetchBookings() {
        viewModel.fetchBookings(currentPage - 1, pageSize);
    }
}
