package com.example.eventplanner.pagination;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.eventplanner.R;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class Pagination {
    private LinearLayout linearLayout;
    private int totalPages;
    private int currentPage;
    private int currentNumberButton;
    private Context context;
    private Button first, previous, next, last;
    private List<Button> numberButtons;
    @Setter
    private OnPaginateListener onPaginateListener;
    public Pagination(Context context, int totalPages, LinearLayout linearLayout) {
        this.context = context;
        this.totalPages = totalPages;
        this.linearLayout = linearLayout;
        if (totalPages != 0)
            initButtons();
    }

    private void initButtons() {
        numberButtons = new ArrayList<>();
        first = constructButton("⏮", true);
        first.setOnClickListener(v -> toFirst());
        previous = constructButton("◀", true);
        previous.setOnClickListener(v -> toPrevious());
        next = constructButton("▶", true);
        next.setOnClickListener(v -> toNext());
        last = constructButton("⏭", true);
        last.setOnClickListener(v -> toLast());
        int[] numbers;
        if (totalPages <= 7) {
            numbers = new int[totalPages];
            for (int i = 0; i < totalPages; i++)
                numbers[i] = i + 1;
        }
        else
            numbers = new int[]{1, 2, 3, 4, 5, -1, totalPages};
        for (int number : numbers) {
            if (number == -1) {
                Button button = constructButton("...", true);
                button.setOnClickListener(v -> {});
                numberButtons.add(button);
            }
            else {
                Button button = constructButton(Integer.toString(number), false);
                button.setOnClickListener(v -> toPage(number));
                numberButtons.add(button);
            }
        }
        linearLayout.addView(first);
        linearLayout.addView(previous);
        for (Button b : numberButtons)
            linearLayout.addView(b);
        linearLayout.addView(next);
        linearLayout.addView(last);

        numberButtons.get(0).setBackgroundResource(R.drawable.pagination_selected_btn_bg);
        currentPage = 1;
        currentNumberButton = 0;
    }

    private void toFirst() {
        toPage(1);
    }
    private void toLast() {
        toPage(totalPages);
    }
    private void toPrevious() {
        toPage(currentPage - 1);
    }
    private void toNext() {
        toPage(currentPage + 1);
    }
    private void toPage(int page) {
        if (currentPage != page && page >= 1 && page <= totalPages) {
            if (onPaginateListener != null) {
                onPaginateListener.OnPaginate(page);
            }
            updatePagination(page);
        }
    }

    private void updatePagination(int newPage) {
        if (totalPages <= 7) {
            numberButtons.get(currentPage-1).setBackgroundResource(R.drawable.pagination_btn_bg);
            numberButtons.get(newPage-1).setBackgroundResource(R.drawable.pagination_selected_btn_bg);
            currentPage = newPage;
            currentNumberButton = newPage-1;
            return;
        }
        numberButtons.get(currentNumberButton).setBackgroundResource(R.drawable.pagination_btn_bg);

        currentPage = newPage;
        int[] numbers;
        if (currentPage <= 4) {
            numbers = new int[]{2, 3, 4, 5, -1};
            currentNumberButton = currentPage - 1;
        } else if (currentPage >= totalPages - 3) {
            numbers = new int[]{-1, totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1};
            currentNumberButton = 6 - (totalPages - currentPage);
        } else {
            numbers = new int[]{-1, currentPage - 1, currentPage, currentPage + 1, -1};
            currentNumberButton = 3;
        }
        for (int i = 0; i < 5; i++) {
            Button button = numberButtons.get(i+1);
            if (numbers[i] == -1) {
                button.setText("...");
                button.getBackground().setAlpha(0);
                button.setOnClickListener(v -> {});
            }
            else {
                int number = numbers[i];
                button.setText(Integer.toString(number));
                button.getBackground().setAlpha(255);
                button.setOnClickListener(v -> toPage(number));
            }
        }
        numberButtons.get(currentNumberButton).setBackgroundResource(R.drawable.pagination_selected_btn_bg);
    }

    public void changeTotalPages(int newTotalPages) {
        if (newTotalPages >= 7 && totalPages >= 7) {
            totalPages = newTotalPages;
            updatePagination(1);
        }
        else {
            linearLayout.removeAllViews();
            totalPages = newTotalPages;
            initButtons();
        }
    }

    private Button constructButton(String text, boolean noBackground) {
        Button button = new Button(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(7);
        button.setLayoutParams(params);
        button.setPadding(10,10,10,10);
        button.setMinWidth(80);
        button.setMinHeight(80);
        button.setMinimumWidth(80);
        button.setMinimumHeight(80);
        button.setTextColor(MaterialColors.getColor(context, R.attr.text_color, Color.GRAY));
        button.setBackgroundResource(R.drawable.pagination_btn_bg);
        if (noBackground)
            button.getBackground().setAlpha(0);
        button.setTypeface(Typeface.MONOSPACE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        button.setText(text);
        return button;
    }
}
