package com.example.eventplanner.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.SimpleCardAdapter;
import com.example.eventplanner.databinding.FragmentAdminUserReportsBinding;
import com.example.eventplanner.model.user.UserReport;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.pagination.Pagination;

import java.util.ArrayList;
import java.util.List;

public class AdminUserReportsFragment extends Fragment {
    private FragmentAdminUserReportsBinding binding;
    private AdminUserReportsViewModel viewModel;
    private SimpleCardAdapter<UserReport> adapter;
    private List<UserReport> userReports = new ArrayList<>();

    // Pagination
    private Pagination pagination;
    private int currentPage = 1;
    private static final int pageSize = 10;
    public static PageMetadata pageMetadata;

    public AdminUserReportsFragment() {
        // Required empty public constructor
    }

    public static AdminUserReportsFragment newInstance() {
        return new AdminUserReportsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminUserReportsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AdminUserReportsViewModel.class);

        pagination = new Pagination(getContext(), 0, binding.paginationUserReports);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchUserReports();
        });

        adapter = new SimpleCardAdapter<>(userReports,
            "Suspend", this::approveReport,
            "Deny", this::denyReport
        );
        RecyclerView recyclerView = binding.recyclerViewUserReports;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getUserReports().observe(getViewLifecycleOwner(), value -> {
            userReports.clear();
            if (value != null) {
                List<UserReport> reports = value.getContent();
                userReports.addAll(reports);
                int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                pageMetadata = value.getPage();
                if (previousTotalPages != pageMetadata.getTotalPages())
                    pagination.changeTotalPages(pageMetadata.getTotalPages());
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });

        viewModel.getReportApproved().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "User suspended successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to suspend user", Toast.LENGTH_SHORT).show();
                restoreReportInList(id);
            }
        });

        viewModel.getReportDeleted().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "Report denied successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to deny report", Toast.LENGTH_SHORT).show();
                restoreReportInList(id);
            }
        });

        fetchUserReports();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding.paginationUserReports.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationUserReports);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchUserReports();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

    private void approveReport(UserReport element) {
        element.setHidden(true);
        adapter.notifyItemChanged(userReports.indexOf(element));
        viewModel.approveReport(element.getId());
    }

    private void denyReport(UserReport element) {
        element.setHidden(true);
        adapter.notifyItemChanged(userReports.indexOf(element));
        viewModel.deleteReport(element.getId());
    }

    private void restoreReportInList(long reportId) {
        for (UserReport element : userReports) {
            if (element.getId().equals(reportId)) {
                element.setHidden(false);
                adapter.notifyItemChanged(userReports.indexOf(element));
                break;
            }
        }
    }

    private void updateEmptyState() {
        TextView emptyText = binding.textEmptyReports;
        if (userReports.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    private void fetchUserReports() {
        viewModel.fetchUserReports(currentPage - 1, pageSize);
    }
}
