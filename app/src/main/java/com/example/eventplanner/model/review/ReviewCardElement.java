package com.example.eventplanner.model.review;

import android.text.format.DateUtils;

import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventReview;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.example.eventplanner.model.serviceproduct.ServiceProductReview;
import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.utils.SimpleCardElement;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCardElement extends SimpleCardElement {
    private Long id;
    private Double grade;
    private String comment;
    private BaseUser user;
    private ReviewStatus reviewStatus;
    private Date createdAt;
    private boolean hiding;
    
    private Event event;
    
    private ServiceProduct serviceProduct;
    
    // Review type to determine which target is being reviewed
    private ReviewType reviewType;

    @Override
    public String getTitle() {
        String reviewerName = getReviewerName();
        String targetName = getTargetName();
        String targetType = getTargetType();
        
        return String.format("**%s** reviewed %s **%s**", reviewerName, targetType, targetName);
    }

    @Override
    public String getSubtitle() {
        return createdAt != null ? DateUtils.getRelativeTimeSpanString(createdAt.getTime()).toString() : "";
    }

    @Override
    public String getBody() {
        StringBuilder body = new StringBuilder();
        body.append("**Rating:** ").append(grade != null ? grade : "N/A").append("\n");
        body.append("**Comment:**\n");
        body.append(comment != null ? comment : "No comment provided");
        return body.toString();
    }

    private String getReviewerName() {
        if (user != null) {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();

            if (firstName != null && lastName != null) {
                return firstName + " " + lastName + " (" + email + ")";
            } else if (firstName != null) {
                return firstName + " (" + email + ")";
            } else {
                return email != null ? email : "Deleted User";
            }
        }
        return "Deleted User";
    }

    private String getTargetName() {
        if (reviewType == ReviewType.EVENT && event != null) {
            return event.getName();
        } else if (reviewType == ReviewType.SERVICE_PRODUCT && serviceProduct != null) {
            return serviceProduct.getName();
        }
        return "Unknown Target";
    }

    private String getTargetType() {
        if (reviewType == ReviewType.EVENT) {
            return "event";
        } else if (reviewType == ReviewType.SERVICE_PRODUCT && serviceProduct != null) {
            String dtype = serviceProduct.getDtype();
            if ("Product".equals(dtype)) {
                return "product";
            } else if ("Service".equals(dtype)) {
                return "service";
            } else {
                return "service/product";
            }
        }
        return "item";
    }

    public static ReviewCardElement fromReview(Review review) {
        ReviewCardElement element = new ReviewCardElement();
        element.setId(review.getId());
        element.setGrade(review.getGrade());
        element.setComment(review.getComment());
        element.setUser(review.getUser());
        element.setReviewStatus(review.getReviewStatus());
        element.setCreatedAt(review.getCreatedAt());
        element.setHiding(review.isHiding());
        element.setHidden(review.isHidden());
        if (review instanceof EventReview) {
            element.setReviewType(ReviewType.EVENT);
            element.setEvent(((EventReview) review).getEvent());
        } else if (review instanceof ServiceProductReview){
            element.setReviewType(ReviewType.SERVICE_PRODUCT);
            element.setServiceProduct(((ServiceProductReview) review).getServiceProduct());
        } else
            element.setReviewType(ReviewType.SERVICE_PRODUCT);
        return element;
    }

    public static ReviewCardElement fromEventReview(EventReview eventReview) {
        ReviewCardElement element = new ReviewCardElement();
        element.setId(eventReview.getId());
        element.setGrade(eventReview.getGrade());
        element.setComment(eventReview.getComment());
        element.setUser(eventReview.getUser());
        element.setReviewStatus(eventReview.getReviewStatus());
        element.setEvent(eventReview.getEvent());
        element.setReviewType(ReviewType.EVENT);
        return element;
    }

    public static ReviewCardElement fromServiceProductReview(ServiceProductReview serviceProductReview) {
        ReviewCardElement element = new ReviewCardElement();
        element.setId(serviceProductReview.getId());
        element.setGrade(serviceProductReview.getGrade());
        element.setComment(serviceProductReview.getComment());
        element.setUser(serviceProductReview.getUser());
        element.setReviewStatus(serviceProductReview.getReviewStatus());
        element.setServiceProduct(serviceProductReview.getServiceProduct());
        element.setReviewType(ReviewType.SERVICE_PRODUCT);
        return element;
    }

    public Long getTargetId() {
        if (reviewType == ReviewType.EVENT && event != null) {
            return event.getId();
        } else if (reviewType == ReviewType.SERVICE_PRODUCT && serviceProduct != null) {
            return serviceProduct.getId();
        }
        return null;
    }
}
