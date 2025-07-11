package com.example.eventplanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;

public class ServiceProductAdapter extends RecyclerView.Adapter<ServiceProductAdapter.ViewHolder> {
    private List<ServiceProductSummaryDto> localDataSet = new ArrayList<>();

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView sppUsername;
        private final TextView sppEmail;
        private final TextView serviceProductName;
        private final TextView serviceProductPrice;
        private final TextView serviceProductDescription;
        private final Button moreInfo;
        private final ImageButton heart;
        private boolean favorite;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            sppUsername = (TextView) view.findViewById(R.id.text_spp_username);
            sppEmail = (TextView) view.findViewById(R.id.text_spp_email);
            serviceProductName = (TextView) view.findViewById(R.id.text_service_product_name);
            serviceProductPrice = (TextView) view.findViewById(R.id.text_service_product_price);
            serviceProductDescription = (TextView) view.findViewById(R.id.text_service_product_description);
            moreInfo = (Button) view.findViewById(R.id.btn_service_product_more_info);
            heart = (ImageButton) view.findViewById(R.id.btn_service_product_heart);

            heart.setOnClickListener(v -> {
                favorite = !favorite;
                v.setBackgroundResource(favorite ? R.drawable.heart_filled : R.drawable.heart_empty);
            });
        }

    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public ServiceProductAdapter(List<ServiceProductSummaryDto> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_service_product_compact, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        ServiceProductSummaryDto dto = localDataSet.get(position);
        viewHolder.getSppUsername().setText(dto.getCreatorName());
        viewHolder.getSppEmail().setText(dto.getCreatorEmail());
        viewHolder.getServiceProductName().setText(dto.getName());
        viewHolder.getServiceProductPrice().setText(
                dto.getPrice() <= 0
                        ? "Contact"
                        : "$" + Double.toString(dto.getPrice()));
        viewHolder.getServiceProductDescription().setText(dto.getDescription());
        viewHolder.getHeart().setBackgroundResource(viewHolder.isFavorite() ? R.drawable.heart_filled : R.drawable.heart_empty);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}