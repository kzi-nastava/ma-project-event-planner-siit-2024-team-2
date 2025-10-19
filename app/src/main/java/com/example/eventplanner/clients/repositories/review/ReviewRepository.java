package com.example.eventplanner.clients.repositories.review;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventplanner.clients.services.review.ReviewService;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.dto.review.ReviewCommentDto;
import com.example.eventplanner.dto.review.ReviewDto;
import com.example.eventplanner.dto.review.ReviewEligibilityDto;
import com.example.eventplanner.dto.review.ReviewStatusDto;
import com.example.eventplanner.dto.review.ReviewSummaryDto;
import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {
    
    private final ReviewService reviewService;
    
    public ReviewRepository() {
        this.reviewService = ClientUtils.reviewService;
    }
    
    public LiveData<List<Review>> getAll() {
        MutableLiveData<List<Review>> liveData = new MutableLiveData<>();
        reviewService.getAll().enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? List.of(response.body()) : new ArrayList<>()),
                error -> liveData.setValue(new ArrayList<>())
        ));
        return liveData;
    }
    
    public LiveData<Review> get(long id) {
        MutableLiveData<Review> liveData = new MutableLiveData<>();
        reviewService.get(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<Review> add(ReviewDto review) {
        MutableLiveData<Review> liveData = new MutableLiveData<>();
        reviewService.add(review).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<Review> update(long id, ReviewDto review) {
        MutableLiveData<Review> liveData = new MutableLiveData<>();
        reviewService.update(id, review).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<Boolean> delete(long id) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        reviewService.delete(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(true),
                error -> liveData.setValue(false)
        ));
        return liveData;
    }
    
    public LiveData<PagedModel<Review>> getAllPending(Integer page, Integer size) {
        MutableLiveData<PagedModel<Review>> liveData = new MutableLiveData<>();
        reviewService.getAllPending(page, size).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<ReviewStatusDto> approve(long id) {
        MutableLiveData<ReviewStatusDto> liveData = new MutableLiveData<>();
        reviewService.approve(id).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<ReviewCommentDto> updateComment(long id, String comment) {
        MutableLiveData<ReviewCommentDto> liveData = new MutableLiveData<>();
        reviewService.updateComment(id, comment).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<PagedModel<ReviewSummaryDto>> getEventReviews(long eventId, Integer page, Integer size) {
        MutableLiveData<PagedModel<ReviewSummaryDto>> liveData = new MutableLiveData<>();
        reviewService.getEventReviews(eventId, page, size).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<ReviewEligibilityDto> getEventReviewEligibility(long eventId) {
        MutableLiveData<ReviewEligibilityDto> liveData = new MutableLiveData<>();
        reviewService.getEventReviewEligibility(eventId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<PagedModel<ReviewSummaryDto>> getServiceProductReviews(long serviceProductId, Integer page, Integer size) {
        MutableLiveData<PagedModel<ReviewSummaryDto>> liveData = new MutableLiveData<>();
        reviewService.getServiceProductReviews(serviceProductId, page, size).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
    
    public LiveData<ReviewEligibilityDto> getServiceProductReviewEligibility(long serviceProductId) {
        MutableLiveData<ReviewEligibilityDto> liveData = new MutableLiveData<>();
        reviewService.getServiceProductReviewEligibility(serviceProductId).enqueue(new SimpleCallback<>(
                response -> liveData.setValue(response != null ? response.body() : null),
                error -> liveData.setValue(null)
        ));
        return liveData;
    }
}
