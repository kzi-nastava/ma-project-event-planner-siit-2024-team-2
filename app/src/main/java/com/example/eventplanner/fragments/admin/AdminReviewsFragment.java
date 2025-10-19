package com.example.eventplanner.fragments.admin;

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
import com.example.eventplanner.databinding.FragmentAdminReviewsBinding;
import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.review.ReviewCardElement;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.pagination.Pagination;
import com.example.eventplanner.utils.JsonLog;

import java.util.ArrayList;
import java.util.List;

public class AdminReviewsFragment extends Fragment {
    private FragmentAdminReviewsBinding binding;
    private AdminReviewsViewModel viewModel;
    private SimpleCardAdapter<ReviewCardElement> adapter;
    private List<ReviewCardElement> reviewCards = new ArrayList<>();

    // Pagination
    private Pagination pagination;
    private int currentPage = 1;
    private static final int pageSize = 10;
    public static PageMetadata pageMetadata;

    public AdminReviewsFragment() {
        // Required empty public constructor
    }

    public static AdminReviewsFragment newInstance() {
        return new AdminReviewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAdminReviewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AdminReviewsViewModel.class);

        pagination = new Pagination(getContext(), 0, binding.paginationReviews);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchReviews();
        });

        adapter = new SimpleCardAdapter<>(reviewCards,
            "Approve", this::approveReview,
            "Reject", this::rejectReview
        );
        RecyclerView recyclerView = binding.recyclerViewReviews;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getReviews().observe(getViewLifecycleOwner(), value -> {
            reviewCards.clear();
            if (value != null) {
                List<Review> reviews = value.getContent();
                // Convert Review objects to ReviewCardElement objects
                for (Review review : reviews) {
                    JsonLog.d("Review", review);
                    ReviewCardElement cardElement = ReviewCardElement.fromReview(review);
                    reviewCards.add(cardElement);
                }
                int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                pageMetadata = value.getPage();
                if (previousTotalPages != pageMetadata.getTotalPages())
                    pagination.changeTotalPages(pageMetadata.getTotalPages());
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });

        viewModel.getReviewApproved().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "Review approved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to approve review", Toast.LENGTH_SHORT).show();
                restoreReviewInList(id);
            }
        });

        viewModel.getReviewDeleted().observe(getViewLifecycleOwner(), value -> {
            long id = value.first;
            boolean success = value.second;
            if (success) {
                Toast.makeText(getContext(), "Review rejected successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to reject review", Toast.LENGTH_SHORT).show();
                restoreReviewInList(id);
            }
        });

        fetchReviews();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding.paginationReviews.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationReviews);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchReviews();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

    private void approveReview(ReviewCardElement element) {
        element.setHidden(true);
        adapter.notifyItemChanged(reviewCards.indexOf(element));
        viewModel.approveReview(element.getId());
    }

    private void rejectReview(ReviewCardElement element) {
        element.setHidden(true);
        adapter.notifyItemChanged(reviewCards.indexOf(element));
        viewModel.deleteReview(element.getId());
    }

    private void restoreReviewInList(long reviewId) {
        for (ReviewCardElement element : reviewCards) {
            if (element.getId().equals(reviewId)) {
                element.setHidden(false);
                adapter.notifyItemChanged(reviewCards.indexOf(element));
                break;
            }
        }
    }

    private void updateEmptyState() {
        TextView emptyText = binding.textEmptyReviews;
        if (reviewCards.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }
    }

    private void fetchReviews() {
        viewModel.fetchReviews(currentPage - 1, pageSize);
    }
}
