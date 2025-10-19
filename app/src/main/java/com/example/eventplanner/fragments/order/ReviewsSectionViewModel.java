package com.example.eventplanner.fragments.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.review.ReviewRepository;
import com.example.eventplanner.dto.review.ReviewEligibilityDto;
import com.example.eventplanner.dto.review.ReviewSummaryDto;
import com.example.eventplanner.model.review.ReviewType;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import lombok.Getter;
import lombok.Setter;

public class ReviewsSectionViewModel extends ViewModel {
    
    private final ReviewRepository reviewRepository = new ReviewRepository();
    private final ObserverTracker tracker = new ObserverTracker();
    
    private final MutableLiveData<ReviewEligibilityDto> eligibility = new MutableLiveData<>();
    private final MutableLiveData<PagedModel<ReviewSummaryDto>> reviews = new MutableLiveData<>();
    
    public LiveData<ReviewEligibilityDto> getEligibility() {
        return eligibility;
    }

    @Getter
    @Setter
    private int currentPage = 1;

    public LiveData<PagedModel<ReviewSummaryDto>> getReviews() {
        return reviews;
    }
    
    public void loadReviewEligibility(long entityId, ReviewType reviewType) {
        if (reviewType == ReviewType.EVENT) {
            tracker.observeOnce(reviewRepository.getEventReviewEligibility(entityId), eligibility, true);
        } else {
            tracker.observeOnce(reviewRepository.getServiceProductReviewEligibility(entityId), eligibility, true);
        }
    }
    
    public void loadReviews(long entityId, ReviewType reviewType, int page, int size) {
        if (reviewType == ReviewType.EVENT) {
            tracker.observeOnce(reviewRepository.getEventReviews(entityId, page, size), reviews, true);
        } else {
            tracker.observeOnce(reviewRepository.getServiceProductReviews(entityId, page, size), reviews, true);
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
