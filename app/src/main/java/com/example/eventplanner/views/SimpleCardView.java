package com.example.eventplanner.views;

import android.content.Context;
import android.text.Layout;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

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
    }

    public SimpleCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {

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

    public void setTitle(Spanned title) {
        titleText.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleText.setText(subtitle);
    }

    public void setBody(String body) {
        bodyText.setText(body);
        bodyUpdated();
    }

    public void setBody(Spanned body) {
        bodyText.setText(body);
        bodyUpdated();
    }

    private void bodyUpdated() {
        bodyText.post(() -> {
            Layout layout = bodyText.getLayout();
            if (layout == null) {
                bodyText.post(this::updateReadMoreVisibility);
            } else {
                updateReadMoreVisibility();
            }
        });
    }

    private void updateReadMoreVisibility() {
        Layout layout = bodyText.getLayout();
        if (layout == null) {
            readMoreButton.setVisibility(GONE);
            readLessButton.setVisibility(GONE);
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
            Log.d("SimpleCardView", "Set readMoreButton visibility to VISIBLE");
            readMoreButton.setVisibility(VISIBLE);
        } else {
            readMoreButton.setVisibility(GONE);
            readLessButton.setVisibility(GONE);
        }
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