package com.example.eventplanner.clients.services.review;

import com.example.eventplanner.dto.review.ReviewCommentDto;
import com.example.eventplanner.dto.review.ReviewDto;
import com.example.eventplanner.dto.review.ReviewEligibilityDto;
import com.example.eventplanner.dto.review.ReviewStatusDto;
import com.example.eventplanner.dto.review.ReviewSummaryDto;
import com.example.eventplanner.model.review.Review;
import com.example.eventplanner.model.utils.PagedModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewService {
    
    @Headers({"Content-Type:application/json"})
    @GET("reviews")
    Call<Review[]> getAll();
    
    @Headers({"Content-Type:application/json"})
    @GET("reviews/{id}")
    Call<Review> get(@Path("id") long id);
    
    @Headers({"Content-Type:application/json"})
    @POST("reviews")
    Call<Review> add(@Body ReviewDto review);
    
    @Headers({"Content-Type:application/json"})
    @PUT("reviews/{id}")
    Call<Review> update(@Path("id") long id, @Body ReviewDto review);
    
    @Headers({"Content-Type:application/json"})
    @DELETE("reviews/{id}")
    Call<Void> delete(@Path("id") long id);
    
    @Headers({"Content-Type:application/json"})
    @GET("reviews/pending")
    Call<PagedModel<Review>> getAllPending(
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    
    @Headers({"Content-Type:application/json"})
    @POST("reviews/{id}/approve")
    Call<ReviewStatusDto> approve(@Path("id") long id);
    
    @Headers({"Content-Type:application/json"})
    @PUT("reviews/{id}/comment")
    Call<ReviewCommentDto> updateComment(@Path("id") long id, @Body String comment);
    
    @Headers({"Content-Type:application/json"})
    @GET("events/{eventId}/reviews")
    Call<PagedModel<ReviewSummaryDto>> getEventReviews(
            @Path("eventId") long eventId,
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    
    @Headers({"Content-Type:application/json"})
    @GET("events/{eventId}/review-eligibility")
    Call<ReviewEligibilityDto> getEventReviewEligibility(@Path("eventId") long eventId);
    
    @Headers({"Content-Type:application/json"})
    @GET("service-products/{serviceProductId}/reviews")
    Call<PagedModel<ReviewSummaryDto>> getServiceProductReviews(
            @Path("serviceProductId") long serviceProductId,
            @Query("page") Integer page,
            @Query("size") Integer size
    );
    
    @Headers({"Content-Type:application/json"})
    @GET("service-products/{serviceProductId}/review-eligibility")
    Call<ReviewEligibilityDto> getServiceProductReviewEligibility(@Path("serviceProductId") long serviceProductId);
}
