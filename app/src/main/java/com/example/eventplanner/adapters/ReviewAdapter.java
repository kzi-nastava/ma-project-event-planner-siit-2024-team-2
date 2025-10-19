package com.example.eventplanner.adapters;


import android.text.Layout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.ImageUtil;
import com.example.eventplanner.dto.review.ReviewSummaryDto;
import com.example.eventplanner.utils.FormatUtil;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.Setter;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewSummaryDto> reviews;
    private static final int COLLAPSED_MAX_LINES = 3;

    public ReviewAdapter() {
        this.reviews = new ArrayList<>();
    }

    public void setReviews(List<ReviewSummaryDto> reviews) {
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_card, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewSummaryDto review = reviews.get(position);
        holder.bind(review, position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfilePicture;
        private TextView tvCreatorName;
        private TextView tvCreatorEmail;
        private TextView tvCreatedAt;
        private LinearLayout llStars;
        private TextView tvComment;
        private MaterialButton btnReadMore, btnReadLess;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.iv_profile_picture);
            tvCreatorName = itemView.findViewById(R.id.tv_creator_name);
            tvCreatorEmail = itemView.findViewById(R.id.tv_creator_email);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            llStars = itemView.findViewById(R.id.ll_stars);
            tvComment = itemView.findViewById(R.id.tv_comment);
            btnReadMore = itemView.findViewById(R.id.button_read_more);
            btnReadLess = itemView.findViewById(R.id.button_read_less);
        }

        public void bind(ReviewSummaryDto review, int position) {
            String creatorName = review.getCreatorName();
            if (creatorName == null || creatorName.isEmpty()) {
                creatorName = "Deleted User";
            }
            tvCreatorName.setText(creatorName);

            String creatorEmail = review.getCreatorEmail();
            if (creatorEmail == null) {
                creatorEmail = "";
            }
            tvCreatorEmail.setText(creatorEmail);

            if (review.getCreatedAt() != null) {
                tvCreatedAt.setText(formatDate(review.getCreatedAt()));
            } else {
                tvCreatedAt.setText("");
            }

            setupStars(review.getGrade());

            String comment = review.getComment();
            if (comment == null) {
                comment = "";
            }
            tvComment.setText(FormatUtil.markdownToSpanned(comment));

            setupReadMoreButton();

            if (review.getCreatorProfilePicture() != null && !review.getCreatorProfilePicture().isBlank())
                Glide.with(itemView.getContext())
                        .load(ImageUtil.getImageUrl(review.getCreatorProfilePicture()))
                        .placeholder(R.drawable.profile_picture)
                        .into(ivProfilePicture);
            else
                ivProfilePicture.setImageResource(R.drawable.profile_picture);
        }

        private void setupStars(Double grade) {
            llStars.removeAllViews();
            
            if (grade == null || grade < 1 || grade > 5) {
                ImageView errorIcon = new ImageView(itemView.getContext());
                errorIcon.setImageResource(R.drawable.ic_error);
                errorIcon.setColorFilter(itemView.getContext().getColor(R.color.red));
                llStars.addView(errorIcon);
                return;
            }

            for (int i = 1; i <= grade; i++) {
                ImageView star = new ImageView(itemView.getContext());
                star.setImageResource(R.drawable.ic_star);
                star.setColorFilter(itemView.getContext().getColor(R.color.primary));
                star.setPadding(0, 0, 4, 0);
                llStars.addView(star);
            }

            if (grade != Math.floor(grade)) {
                ImageView halfStar = new ImageView(itemView.getContext());
                halfStar.setImageResource(R.drawable.ic_star_half);
                halfStar.setColorFilter(itemView.getContext().getColor(R.color.primary));
                halfStar.setPadding(0, 0, 4, 0);
                llStars.addView(halfStar);
            }

            for (int i = (int) Math.ceil(grade) + 1; i <= 5; i++) {
                ImageView emptyStar = new ImageView(itemView.getContext());
                emptyStar.setImageResource(R.drawable.ic_star_border);
                emptyStar.setColorFilter(itemView.getContext().getColor(R.color.primary));
                emptyStar.setPadding(0, 0, 4, 0);
                llStars.addView(emptyStar);
            }
        }

        private void setupReadMoreButton() {
            // Read More / Less
            tvComment.setMaxLines(COLLAPSED_MAX_LINES);
            btnReadMore.setVisibility(View.GONE);
            btnReadLess.setVisibility(View.GONE);

            btnReadMore.setOnClickListener(v -> {
                tvComment.setMaxLines(Integer.MAX_VALUE);
                btnReadMore.setVisibility(View.GONE);
                btnReadLess.setVisibility(View.VISIBLE);
            });

            btnReadLess.setOnClickListener(v -> {
                tvComment.setMaxLines(COLLAPSED_MAX_LINES);
                btnReadMore.setVisibility(View.VISIBLE);
                btnReadLess.setVisibility(View.GONE);
            });

            tvComment.post(() -> {
                Layout layout = tvComment.getLayout();
                if (layout == null)
                    tvComment.post(this::updateReadMoreVisibility);
                else
                    updateReadMoreVisibility();
            });
        }

        private void updateReadMoreVisibility() {
            Layout layout = tvComment.getLayout();
            if (layout == null) {
                btnReadMore.setVisibility(View.GONE);
                btnReadLess.setVisibility(View.GONE);
                return;
            }

            boolean ellipsized = false;
            for (int i = 0; i < layout.getLineCount(); i++) {
                if (layout.getEllipsisCount(i) > 0) {
                    ellipsized = true;
                    break;
                }
            }
            if (ellipsized) {
                btnReadMore.setVisibility(View.VISIBLE);
            } else {
                btnReadMore.setVisibility(View.GONE);
                btnReadLess.setVisibility(View.GONE);
            }
        }

        private String formatDate(Date date) {
            if (date == null) return "";
            return DateUtils.getRelativeTimeSpanString(date.getTime()).toString();
        }
    }
}
