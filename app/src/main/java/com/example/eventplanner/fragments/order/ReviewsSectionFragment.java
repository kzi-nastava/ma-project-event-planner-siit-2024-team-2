package com.example.eventplanner.fragments.order;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.adapters.ReviewAdapter;
import com.example.eventplanner.databinding.FragmentReviewsSectionBinding;
import com.example.eventplanner.dialogs.AddReviewDialog;
import com.example.eventplanner.dto.review.ReviewEligibilityDto;
import com.example.eventplanner.dto.review.ReviewSummaryDto;
import com.example.eventplanner.model.review.ReviewType;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.pagination.Pagination;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class ReviewsSectionFragment extends Fragment {

    private static final String ARG_ENTITY_ID = "entity_id";
    private static final String ARG_ENTITY_NAME = "entity_name";
    private static final String ARG_REVIEW_TYPE = "review_type";

    private FragmentReviewsSectionBinding binding;
    private ReviewsSectionViewModel viewModel;
    private ReviewAdapter reviewAdapter;
    private Pagination pagination;

    private long entityId;
    private String entityName;
    private ReviewType reviewType;
    private int currentPage = 1;
    private int pageSize = 10;
    private PageMetadata pageMetadata;
    private boolean canReview = false;
    private String eligibilityReason = "";

    public static ReviewsSectionFragment newInstance(long entityId, String entityName, ReviewType reviewType) {
        ReviewsSectionFragment fragment = new ReviewsSectionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ENTITY_ID, entityId);
        args.putString(ARG_ENTITY_NAME, entityName);
        args.putSerializable(ARG_REVIEW_TYPE, reviewType);
        fragment.setArguments(args);
        Log.e("ReviewsSectionFragment", "Entity id is " + entityId);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entityId = getArguments().getLong(ARG_ENTITY_ID);
            entityName = getArguments().getString(ARG_ENTITY_NAME);
            reviewType = (ReviewType) getArguments().getSerializable(ARG_REVIEW_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReviewsSectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        setupRecyclerView();
        setupClickListeners();
        setupViewModel();
        setupPagination();
        loadReviewEligibility();
        loadReviews();

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView rvReviews = binding.rvReviews;
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewAdapter = new ReviewAdapter();
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupClickListeners() {
        MaterialButton btnAddReview = binding.btnAddReview;
        btnAddReview.setOnClickListener(v -> openAddReviewDialog());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ReviewsSectionViewModel.class);
        
        viewModel.getEligibility().observe(getViewLifecycleOwner(), this::handleEligibilityResponse);
        viewModel.getReviews().observe(getViewLifecycleOwner(), this::handleReviewsResponse);
    }

    private void setupPagination() {
        pagination = new Pagination(requireContext(), 0, binding.llPagination);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            loadReviews();
        });
    }

    private void loadReviewEligibility() {
        showLoading(true);
        viewModel.loadReviewEligibility(entityId, reviewType);
    }

    private void handleEligibilityResponse(ReviewEligibilityDto eligibility) {
        if (eligibility != null) {
            canReview = eligibility.isCanReview();
            eligibilityReason = eligibility.getReason() != null ? eligibility.getReason() : "Cannot review";
        } else {
            canReview = false;
            eligibilityReason = "Unable to check eligibility";
        }
        updateEligibilityUI();
    }

    private void updateEligibilityUI() {
        MaterialButton btnAddReview = binding.btnAddReview;
        TextView tvEligibilityMessage = binding.tvEligibilityMessage;
        
        btnAddReview.setEnabled(canReview);
        
        if (!canReview) {
            tvEligibilityMessage.setText(eligibilityReason);
            tvEligibilityMessage.setVisibility(View.VISIBLE);
        } else {
            tvEligibilityMessage.setVisibility(View.GONE);
        }
    }

    private void loadReviews() {
        showLoading(true);
        viewModel.loadReviews(entityId, reviewType, currentPage - 1, pageSize);
    }

    private void handleReviewsResponse(PagedModel<ReviewSummaryDto> pagedReviews) {
        showLoading(false);
        
        if (pagedReviews != null && pagedReviews.getContent() != null) {
            List<ReviewSummaryDto> reviews = pagedReviews.getContent();
            reviewAdapter.setReviews(reviews);


            int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pageMetadata = pagedReviews.getPage();
            if (previousTotalPages != pageMetadata.getTotalPages())
                pagination.changeTotalPages(pageMetadata.getTotalPages());

            TextView tvNoReviews = binding.tvNoReviews;
            if (reviews.isEmpty()) {
                tvNoReviews.setVisibility(View.VISIBLE);
            } else {
                tvNoReviews.setVisibility(View.GONE);
            }
        } else {
            reviewAdapter.setReviews(new ArrayList<>());
            TextView tvNoReviews = binding.tvNoReviews;
            tvNoReviews.setVisibility(View.VISIBLE);
            pageMetadata = null;
            pagination.changeTotalPages(0);
        }
    }

    private void openAddReviewDialog() {
        AddReviewDialog dialog = AddReviewDialog.newInstance(entityId, entityName, reviewType);
        dialog.setOnReviewAddedListener(() -> {
            loadReviews();
            viewModel.loadReviewEligibility(entityId, reviewType);
        });
        dialog.show(getParentFragmentManager(), "AddReviewDialog");
    }

    private void showLoading(boolean show) {
        CircularProgressIndicator progress = binding.progressReviews;
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (binding.llPagination.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.llPagination);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                loadReviews();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

}