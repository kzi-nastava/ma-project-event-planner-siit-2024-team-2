package com.example.eventplanner.fragments.communication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.adapters.SimpleCardAdapter;
import com.example.eventplanner.databinding.FragmentNotificationsBinding;
import com.example.eventplanner.model.communication.Notification;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.pagination.Pagination;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private SimpleCardAdapter<Notification> adapter;
    private List<Notification> notifications = new ArrayList<>();

    // Pagination
    private Pagination pagination;
    private int currentPage = 1;
    private static final int pageSize = 10;
    public static PageMetadata pageMetadata;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        pagination = new Pagination(getContext(), 0, binding.paginationNotifications);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            viewModel.fetchNotifications(currentPage, pageSize, Instant.now().toString());
        });

        adapter = new SimpleCardAdapter<>(notifications,
            "Dismiss", this::dismiss
        );

        viewModel.getNotifications().observe(getViewLifecycleOwner(), value -> {
            notifications.clear();
            if (value != null) {
                notifications.addAll(value.getContent());
                int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                pageMetadata = value.getPage();
                if (previousTotalPages != pageMetadata.getTotalPages())
                    pagination.changeTotalPages(pageMetadata.getTotalPages());
            }
            adapter.notifyDataSetChanged();
        });

        viewModel.getNotificationDismissed().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (!success) {
                Toast.makeText(getContext(), "Failed to dismiss notification", Toast.LENGTH_SHORT).show();
                notifications.stream().filter(notification -> notification.getId() == id).findFirst().ifPresent(notification -> {
                        notification.setHidden(false);
                        adapter.notifyItemChanged(notifications.indexOf(notification));
                });
            }
        });

        viewModel.fetchNotifications(currentPage - 1, pageSize, Instant.now().toString());

        return root;
    }

    private void dismiss(Notification notification) {
        notification.setHidden(true);
        adapter.notifyItemChanged(notifications.indexOf(notification));
        viewModel.dismissNotification(notification.getId());
    }
}