package com.example.eventplanner.fragments.admin;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.review.ReviewRepository;
import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.ObserverTracker;

import lombok.Getter;
import lombok.Setter;

public class AdminReviewsViewModel extends ViewModel {
    private final ReviewRepository reviewRepository = new ReviewRepository();

    @Getter
    private final MutableLiveData<PagedModel<Review>> reviews = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> reviewApproved = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Pair<Long, Boolean>> reviewDeleted = new MutableLiveData<>();
    private final ObserverTracker tracker = new ObserverTracker();
    
    @Setter
    @Getter
    private int currentPage = 1;

    public void fetchReviews(Integer page, Integer size) {
        tracker.observeOnce(reviewRepository.getAllPending(page, size), reviews, true);
    }

    public void approveReview(Long id) {
        tracker.observeOnce(reviewRepository.approve(id),
                response -> {
                    boolean success = response != null;
                    reviewApproved.setValue(new Pair<>(id, success));
                });
    }

    public void deleteReview(Long id) {
        tracker.observeOnce(reviewRepository.delete(id),
                success -> reviewDeleted.setValue(new Pair<>(id, success)));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
