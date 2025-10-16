package com.example.eventplanner.fragments.event;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventAdapter;
import com.example.eventplanner.adapters.MyEventAdapter;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.databinding.FragmentMyEventsBinding;
import com.example.eventplanner.dto.event.EventDto;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.pagination.Pagination;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MyEventsFragment extends Fragment {
    public static List<EventSummaryDto> events = new ArrayList<>();
    public static PageMetadata pageMetadata;

    private MyEventsViewModel viewModel;
    private FragmentMyEventsBinding binding;
    private RecyclerView recyclerView;
    private MyEventAdapter adapter;
    private Pagination pagination;
    private CircularProgressIndicator progressIndicator;
    private int currentPage = 1;
    private static final int pageSize = 10;

    public static MyEventsFragment newInstance() {
        return new MyEventsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MyEventsViewModel.class);

        binding = FragmentMyEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerViewMyEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new MyEventAdapter(events, new MyEventAdapter.OnEventActionListener() {
            @Override
            public void onViewDetails(int position, long eventId) {
                Bundle args = new Bundle();
                args.putLong("eventId", eventId);

                // Navigate using NavController from a view inside the fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.editEventFragment, args);
            }


            @Override
            public void onDelete(int position, long eventId) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Call<Void> call = ClientUtils.eventService.deleteEvent(eventId);
                            call.enqueue(new SimpleCallback<>(
                                    response -> {
                                        // Remove by id — safer than relying on 'position' which may become stale
                                        boolean removed = adapter.removeById(eventId);
                                        if (!removed) {
                                            // fallback: refresh list from backend
                                            fetchMyEvents();
                                        }
                                        Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                                    },
                                    error -> {
                                        Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
                                    }
                            ));
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onAgenda(int position, long eventId) {
                Bundle args = new Bundle();
                args.putLong("eventId", eventId);
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.agendaFragment, args);
            }
        });
        recyclerView.setAdapter(adapter);



        progressIndicator = binding.progressMyEvents;

        pagination = new Pagination(getContext(), 0, binding.paginationMyEvents);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchMyEvents();
        });

        fetchMyEvents();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding.paginationMyEvents.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationMyEvents);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchMyEvents();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchMyEvents() {
        // you can extend sorting/filters later like AllEventsPage
        Call<PagedModel<EventSummaryDto>> call = ClientUtils.eventService.getMyEventSummaries(
                currentPage - 1,
                pageSize,
                "date",
                SortDirection.DESC,
                "", "", // name, description filters
                null, null, null,
                null, null, null, null,
                null, null
        );

        events.clear();
        progressIndicator.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();

        call.enqueue(new SimpleCallback<>(
                response -> {
                    events.clear();
                    if (response.body() != null) {
                        events.addAll(response.body().getContent());
                        int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                        pageMetadata = response.body().getPage();
                        if (previousTotalPages != pageMetadata.getTotalPages())
                            pagination.changeTotalPages(pageMetadata.getTotalPages());
                        progressIndicator.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    progressIndicator.setVisibility(View.GONE);
                }
        ));
    }
}
