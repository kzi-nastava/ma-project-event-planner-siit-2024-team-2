package com.example.eventplanner.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.eventplanner.adapters.EventAdapter;
import com.example.eventplanner.adapters.ServiceProductAdapter;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.databinding.FragmentHomeBinding;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    EventAdapter eventAdapter;
    ServiceProductAdapter serviceProductAdapter;
    RecyclerView recyclerViewEvents, recyclerViewServiceProducts;
    RelativeLayout progressContainerEvents, progressContainerServiceProducts;
    List<EventSummaryDto> events = new ArrayList<>();
    List<ServiceProductSummaryDto> serviceProducts = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewEvents = binding.recyclerViewEvents;
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false));
        eventAdapter = new EventAdapter(events);
        recyclerViewEvents.setAdapter(eventAdapter);

        recyclerViewServiceProducts = binding.recyclerViewServiceProducts;
        recyclerViewServiceProducts.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false));
        serviceProductAdapter = new ServiceProductAdapter(serviceProducts);
        recyclerViewServiceProducts.setAdapter(serviceProductAdapter);

        progressContainerEvents = binding.progressContainerEvents;
        progressContainerServiceProducts = binding.progressContainerServiceProducts;

        fetchTop5Events();
        fetchTop5ServiceProducts();

        return root;
    }
    private void fetchTop5Events(){
        Call<List<EventSummaryDto>> call = ClientUtils.eventService.getTop5();

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<EventSummaryDto>> call, @NonNull Response<List<EventSummaryDto>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        events.clear();
                        events.addAll(response.body());
                    }
                    else
                        events.clear();
                    progressContainerEvents.setVisibility(View.GONE);
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Log.e("RetrofitCall", "Failed to fetch events. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EventSummaryDto>> call, @NonNull Throwable t) {
                Log.e("RetrofitCall", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
    private void fetchTop5ServiceProducts(){
        Call<List<ServiceProductSummaryDto>> call = ClientUtils.serviceProductService.getTop5();

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<ServiceProductSummaryDto>> call, @NonNull Response<List<ServiceProductSummaryDto>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        serviceProducts.clear();
                        serviceProducts.addAll(response.body());
                    }
                    else
                        serviceProducts.clear();
                    progressContainerServiceProducts.setVisibility(View.GONE);
                    serviceProductAdapter.notifyDataSetChanged();
                } else {
                    Log.e("RetrofitCall", "Failed to fetch serviceProducts. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ServiceProductSummaryDto>> call, @NonNull Throwable t) {
                Log.e("RetrofitCall", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}