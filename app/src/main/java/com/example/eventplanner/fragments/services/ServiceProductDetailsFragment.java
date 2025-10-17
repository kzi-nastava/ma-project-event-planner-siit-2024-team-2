package com.example.eventplanner.fragments.services;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ImageAdapter;
import com.example.eventplanner.adapters.PhotosAdapter;
import com.example.eventplanner.databinding.FragmentServiceProductDetailsBinding;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProduct;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.Getter;

public class ServiceProductDetailsFragment extends Fragment {

   private ServiceProductSummaryDto serviceProduct;
   private List<String> images = new ArrayList<>();
   private Long serviceProductId;

   // UI references
   private TextView tvName, tvDescription, tvCategory, tvPrice, tvDiscount,
           tvAvailable, tvSpecifics, tvReservationDeadline, tvCancellationDeadline, tvDuration, tvAutomaticReservationAbility;

   private FragmentServiceProductDetailsBinding binding;
   private ServiceProductDetailsViewModel viewModel;
   private ImageAdapter adapter;
   private CircularProgressIndicator progressIndicator;
   private RecyclerView recyclerView;
   private LinearLayout detailsLayout;
   private LinearLayout serviceDetails;

   public ServiceProductDetailsFragment() {
      // Required empty constructor
   }

   public static ServiceProductDetailsFragment newInstance(ServiceProductSummaryDto dto) {
      ServiceProductDetailsFragment fragment = new ServiceProductDetailsFragment();
      Bundle args = new Bundle();
      args.putSerializable("serviceProduct", dto);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_service_product_details, container, false);
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      binding = FragmentServiceProductDetailsBinding.bind(view);
      viewModel = new ViewModelProvider(this).get(ServiceProductDetailsViewModel.class);

      tvName = binding.tvSpName;
      tvDescription = binding.tvSpDescription;
      tvCategory = binding.tvSpCategory;
      tvPrice = binding.tvSpPrice;
      tvDiscount = binding.tvSpDiscount;
      tvAvailable = binding.tvSpAvailable;

      tvSpecifics = binding.tvSpSpecifics;
      tvReservationDeadline = binding.tvSpReservationDeadline;
      tvCancellationDeadline = binding.tvSpCancellationDeadline;
      tvDuration = binding.tvSpDuration;
      tvAutomaticReservationAbility = binding.tvSpAutomaticReservationAbility;

      progressIndicator = binding.progress;
      progressIndicator.setVisibility(View.VISIBLE);

      detailsLayout = binding.detailsLayout;
      detailsLayout.setVisibility(View.GONE);

      serviceDetails = binding.serviceDetails;
      serviceDetails.setVisibility(View.GONE);

      recyclerView = binding.rvImages;
      recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
      adapter = new ImageAdapter(images);
      recyclerView.setAdapter(adapter);

      if (getArguments() != null) {
         serviceProductId = (Long )getArguments().getSerializable("serviceProductId");
         if (serviceProductId != null)
            viewModel.getServiceProductById(serviceProductId).observe(getViewLifecycleOwner(), this::bindData);
      }
   }

   @SuppressLint("SetTextI18n")
   private void bindData(ServiceProduct dto) {
      tvName.setText(dto.getName());
      tvDescription.setText(dto.getDescription());
      tvCategory.setText(dto.getCategory() != null ? dto.getCategory().getName() : "N/A");
      tvPrice.setText(String.format(Locale.getDefault(), "%.2f €", dto.getPrice()));
      tvDiscount.setText(String.format(Locale.getDefault(), "%.2f €", dto.getDiscount()));
      tvAvailable.setText(dto.isAvailable() ? "Yes" : "No");

      images.clear();
      if (dto instanceof Service) {
         serviceDetails.setVisibility(View.VISIBLE);
         Service serviceDto = (Service) dto;
         tvSpecifics.setText(" " + serviceDto.getSpecifies());
         tvReservationDeadline.setText(" " + getString(R.string.number_days, serviceDto.getReservationDaysDeadline()));
         tvCancellationDeadline.setText(" " + getString(R.string.number_days, serviceDto.getCancellationDaysDeadline()));
         tvDuration.setText(" " + String.format(Locale.getDefault(), "%.2f hours", serviceDto.getDuration()));
         tvAutomaticReservationAbility.setText(serviceDto.isHasAutomaticReservation() ? " Yes" : " No");
         images.addAll(serviceDto.getImageEncodedNames());
      }
      else
         images.addAll(dto.getImages());

      progressIndicator.setVisibility(View.GONE);
      detailsLayout.setVisibility(View.VISIBLE);
   }
}
