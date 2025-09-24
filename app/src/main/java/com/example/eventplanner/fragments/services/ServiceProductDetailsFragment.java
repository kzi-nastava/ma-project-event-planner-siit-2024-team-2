package com.example.eventplanner.fragments.services;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventplanner.R;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

public class ServiceProductDetailsFragment extends Fragment {

   private ServiceProductSummaryDto serviceProduct;

   // UI references
   private TextView tvName, tvDescription, tvCategory, tvPrice, tvDiscount,
           tvAvailable, tvCreatorName, tvCreatorEmail;

   public ServiceProductDetailsFragment() {
      // Required empty constructor
   }

   public static ServiceProductDetailsFragment newInstance(ServiceProductSummaryDto dto) {
      ServiceProductDetailsFragment fragment = new ServiceProductDetailsFragment();
      Bundle args = new Bundle();
      args.putSerializable("serviceProduct", dto); // make sure dto implements Serializable
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_service_product_details, container, false);
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      // Bind UI
      tvName = view.findViewById(R.id.tv_sp_name);
      tvDescription = view.findViewById(R.id.tv_sp_description);
      tvCategory = view.findViewById(R.id.tv_sp_category);
      tvPrice = view.findViewById(R.id.tv_sp_price);
      tvDiscount = view.findViewById(R.id.tv_sp_discount);
      tvAvailable = view.findViewById(R.id.tv_sp_available);
      tvCreatorName = view.findViewById(R.id.tv_sp_creator_name);
      tvCreatorEmail = view.findViewById(R.id.tv_sp_creator_email);

      // Retrieve DTO
      if (getArguments() != null) {
         serviceProduct = (ServiceProductSummaryDto) getArguments().getSerializable("serviceProduct");
         if (serviceProduct != null) {
            bindData(serviceProduct);
         }
      }
   }

   private void bindData(ServiceProductSummaryDto dto) {
      tvName.setText(dto.getName());
      tvDescription.setText(dto.getDescription());
      tvCategory.setText(dto.getCategory() != null ? dto.getCategory().getName() : "N/A");
      tvPrice.setText(String.format("$%.2f", dto.getPrice()));
      tvDiscount.setText(String.format("%.0f%%", dto.getDiscount()));
      tvAvailable.setText(dto.isAvailable() ? "Yes" : "No");
      tvCreatorName.setText(dto.getCreatorName());
      tvCreatorEmail.setText(dto.getCreatorEmail());
   }
}
