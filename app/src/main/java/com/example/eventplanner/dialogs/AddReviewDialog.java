package com.example.eventplanner.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.repositories.review.ReviewRepository;
import com.example.eventplanner.dto.review.ReviewDto;
import com.example.eventplanner.model.review.ReviewType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class AddReviewDialog extends DialogFragment {

    public interface OnReviewAddedListener {
        void onReviewAdded();
    }

    private static final String ARG_ENTITY_ID = "entity_id";
    private static final String ARG_ENTITY_NAME = "entity_name";
    private static final String ARG_REVIEW_TYPE = "review_type";

    private long entityId;
    private String entityName;
    private ReviewType reviewType;
    private float selectedRating = 0f;
    private OnReviewAddedListener listener;
    private ReviewRepository reviewRepository;

    private LinearLayout llRatingStars;
    private TextInputEditText etComment;
    private MaterialButton btnSubmit;
    private MaterialButton btnCancel;

    public static AddReviewDialog newInstance(long entityId, String entityName, ReviewType reviewType) {
        AddReviewDialog dialog = new AddReviewDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_ENTITY_ID, entityId);
        args.putString(ARG_ENTITY_NAME, entityName);
        args.putSerializable(ARG_REVIEW_TYPE, reviewType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entityId = getArguments().getLong(ARG_ENTITY_ID);
            entityName = getArguments().getString(ARG_ENTITY_NAME);
            reviewType = (ReviewType) getArguments().getSerializable(ARG_REVIEW_TYPE);
        }
        reviewRepository = new ReviewRepository();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_review, null);

        setupViews(view);
        setupRatingStars();
        setupClickListeners();

        builder.setView(view);
        return builder.create();
    }

    private void setupViews(View view) {
        llRatingStars = view.findViewById(R.id.ll_rating_stars);
        etComment = view.findViewById(R.id.et_comment);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);

        TextView tvEntityName = view.findViewById(R.id.tv_entity_name);
        tvEntityName.setText(entityName);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRatingStars() {
        llRatingStars.removeAllViews();

        for (int i = 1; i <= 5; i++) {
            ImageView star = new ImageView(requireContext());
            star.setImageResource(R.drawable.ic_star_border);
            star.setColorFilter(requireContext().getColor(R.color.primary));
            star.setPadding(0, 0, 8, 0);

            final int starPosition = i;
            star.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    float touchX = event.getX();
                    float starWidth = v.getWidth();

                    // Determine if left half or right half was touched
                    if (touchX < starWidth / 2) {
                        // Left half
                        selectedRating = starPosition - 0.5f;
                    } else {
                        // Right half
                        selectedRating = starPosition;
                    }

                    if (selectedRating < 1f) {
                        selectedRating = 1f;
                    }

                    updateRatingDisplay();
                    updateSubmitButton();
                    return true;
                }
                return false;
            });

            llRatingStars.addView(star);
        }
    }

    private void updateRatingDisplay() {
        int fullStars = (int) selectedRating;
        boolean hasHalfStar = (selectedRating % 1) != 0;

        for (int i = 0; i < llRatingStars.getChildCount(); i++) {
            ImageView star = (ImageView) llRatingStars.getChildAt(i);

            if (i < fullStars) {
                // Full star
                star.setImageResource(R.drawable.ic_star);
            } else if (i == fullStars && hasHalfStar) {
                // Half star
                star.setImageResource(R.drawable.ic_star_half);
            } else {
                // Empty star
                star.setImageResource(R.drawable.ic_star_border);
            }
        }
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void updateSubmitButton() {
        btnSubmit.setEnabled(selectedRating >= 1f);
    }

    private void submitReview() {
        if (selectedRating < 1f) {
            Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setGrade(selectedRating);
        reviewDto.setComment(comment.isEmpty() ? null : comment);
        reviewDto.setEntityId(entityId);
        reviewDto.setReviewType(reviewType);

        reviewRepository.add(reviewDto).observe(this, review -> {
            if (review != null && review.first != null) {
                Toast.makeText(requireContext(), "Review submitted successfully", Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onReviewAdded();
                }
                dismiss();
            } else {
                if (review == null || review.second == null)
                    Toast.makeText(requireContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(requireContext(), review.second, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOnReviewAddedListener(OnReviewAddedListener listener) {
        this.listener = listener;
    }
}