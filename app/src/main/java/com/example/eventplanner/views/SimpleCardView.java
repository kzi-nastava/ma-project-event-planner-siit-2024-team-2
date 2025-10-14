package com.example.eventplanner.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.example.eventplanner.R;

public class SimpleCardView extends MaterialCardView {
    private TextView titleText, subtitleText, bodyText;
    private MaterialButton readMoreButton, readLessButton;
    private MaterialButton actionButton1, actionButton2;

    private static final int COLLAPSED_MAX_LINES = 4;

    public SimpleCardView(Context context) {
        super(context);
        init(context);
    }

    public SimpleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimpleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_simple_card, this, true);

        titleText = findViewById(R.id.text_card_title);
        subtitleText = findViewById(R.id.text_card_subtitle);
        bodyText = findViewById(R.id.text_card_body);

        readMoreButton = findViewById(R.id.button_read_more);
        readLessButton = findViewById(R.id.button_read_less);

        actionButton1 = findViewById(R.id.button_card_action_1);
        actionButton2 = findViewById(R.id.button_card_action_2);

        // Read More / Less
        bodyText.setMaxLines(COLLAPSED_MAX_LINES);
        readMoreButton.setVisibility(GONE);
        readLessButton.setVisibility(GONE);

        readMoreButton.setOnClickListener(v -> {
            bodyText.setMaxLines(Integer.MAX_VALUE);
            readMoreButton.setVisibility(GONE);
            readLessButton.setVisibility(VISIBLE);
        });

        readLessButton.setOnClickListener(v -> {
            bodyText.setMaxLines(COLLAPSED_MAX_LINES);
            readMoreButton.setVisibility(VISIBLE);
            readLessButton.setVisibility(GONE);
        });
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleText.setText(subtitle);
    }

    public void setBody(String body) {
        bodyText.setText(body);
        bodyText.post(() -> {
            if (bodyText.getLineCount() > COLLAPSED_MAX_LINES) {
                readMoreButton.setVisibility(VISIBLE);
            } else {
                readMoreButton.setVisibility(GONE);
                readLessButton.setVisibility(GONE);
            }
        });
    }

    public void setActionButton1(String text, View.OnClickListener listener) {
        actionButton1.setVisibility(View.VISIBLE);
        actionButton1.setText(text);
        actionButton1.setOnClickListener(listener);
    }

    public void setActionButton2(String text, View.OnClickListener listener) {
        actionButton2.setVisibility(View.VISIBLE);
        actionButton2.setText(text);
        actionButton2.setOnClickListener(listener);
    }

    public void hideActionButton2() {
        actionButton2.setVisibility(View.GONE);
    }
}