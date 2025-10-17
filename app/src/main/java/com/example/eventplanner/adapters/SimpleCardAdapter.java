package com.example.eventplanner.adapters;

import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.model.utils.SimpleCardElement;
import com.example.eventplanner.utils.FormatUtil;
import com.example.eventplanner.views.SimpleCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;

public class SimpleCardAdapter<T extends SimpleCardElement> extends RecyclerView.Adapter<SimpleCardAdapter.ViewHolder>{

    private List<T> localDataSet;
    private final Consumer<T> action1, action2;
    private final String action1Text, action2Text;
    private int bottomMargin = 0;

    public SimpleCardAdapter(List<T> dataSet, String action1Text, Consumer<T> action1) {
        this(dataSet, action1Text, action1, null, null);
    }

    public SimpleCardAdapter(List<T> dataSet, String action1Text, Consumer<T> action1, String action2Text, Consumer<T> action2) {
        this.localDataSet = dataSet != null ? dataSet : new ArrayList<>();
        this.action1Text = action1Text;
        this.action1 = action1;
        this.action2Text = action2Text;
        this.action2 = action2;
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final SimpleCardView view;

        public ViewHolder(SimpleCardView view) {
            super(view);
            this.view = view;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        SimpleCardView view = (SimpleCardView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_simple_card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T element = localDataSet.get(position);
        SimpleCardView view = holder.view;

        Spanned title = FormatUtil.markdownToSpanned(element.getTitle());
        view.setTitle(title);
        view.setSubtitle(element.getSubtitle());
        Spanned body = FormatUtil.markdownToSpanned(element.getBody());
        view.setBody(body);
        view.setActionButton1(action1Text, v -> action1.accept(element));
        if (action2Text != null && action2 != null)
            view.setActionButton2(action2Text, v -> action2.accept(element));
        else
            view.hideActionButton2();

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

        if (view.getTag(R.id.original_bottom_margin) == null)
            view.setTag(R.id.original_bottom_margin, params.bottomMargin);
        int originalBottomMargin = (int) view.getTag(R.id.original_bottom_margin);

        if (element.isHidden()) {
            params.height = 0;
            params.bottomMargin = 0;
            view.setVisibility(View.GONE);
        }
        else {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.bottomMargin = originalBottomMargin;
            view.setVisibility(View.VISIBLE);
        }
        view.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void updateData(List<T> elements) {
        this.localDataSet = elements != null ? elements : new ArrayList<>();
        notifyDataSetChanged();
    }
}
