package com.example.eventplanner.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventAdapter;
import com.example.eventplanner.adapters.ServiceProductAdapter;
import com.example.eventplanner.clients.repositories.user.ProfileRepository;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.databinding.FragmentHomeBinding;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.utils.SimpleCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

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
        eventAdapter = new EventAdapter(events, new EventAdapter.OnEventClickListener() {
            @Override
            public void onMoreInfoClick(EventSummaryDto event) {
                EventDetailsFragment detailsFragment = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putLong("eventId", event.getId());
                detailsFragment.setArguments(args);                  // Navigate using NavController from a view inside the fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.fragment_event_details, args);
            }
            @Override
            public void onHeartClick(EventSummaryDto event, boolean isFavorite) {
                long userId = UserIdUtils.getUserId(getContext()); // get current user ID
                ProfileRepository profileRepository = new ProfileRepository();

                if (isFavorite) {
                    // Add to favorites
                    profileRepository.addFavoriteEvent(userId, event.getId())
                            .observeForever(success -> {
                                if (success) {
                                    event.setFavorite(true);
                                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                                    eventAdapter.notifyItemChanged(events.indexOf(event));
                                } else {
                                    Toast.makeText(getContext(), "Failed to add favorite", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Remove from favorites
                    profileRepository.removeFavoriteEvent(userId, event.getId())
                            .observeForever(success -> {
                                if (success) {
                                    event.setFavorite(false);
                                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                                    eventAdapter.notifyItemChanged(events.indexOf(event));
                                } else {
                                    Toast.makeText(getContext(), "Failed to remove favorite", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

        }
        );
        recyclerViewEvents.setAdapter(eventAdapter);

        recyclerViewServiceProducts = binding.recyclerViewServiceProducts;
        recyclerViewServiceProducts.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false));
        serviceProductAdapter = new ServiceProductAdapter(serviceProducts, new ServiceProductAdapter.OnServiceProductClickListener() {

            @Override
            public void onMoreInfoClick(ServiceProductSummaryDto serviceProduct) {
                EventDetailsFragment detailsFragment = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putLong("serviceProductId", serviceProduct.getId());
                detailsFragment.setArguments(args);                  // Navigate using NavController from a view inside the fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.fragment_service_product_details, args);
            }
            @Override
            public void onHeartClick(ServiceProductSummaryDto event, boolean isFavorite) {

            }
        }
        );
        recyclerViewServiceProducts.setAdapter(serviceProductAdapter);

        progressContainerEvents = binding.progressContainerEvents;
        progressContainerServiceProducts = binding.progressContainerServiceProducts;

        fetchTop5Events();
        fetchTop5ServiceProducts();

        return root;
    }
    @SuppressLint("NotifyDataSetChanged")
    private void fetchTop5Events(){
        Call<List<EventSummaryDto>> call = ClientUtils.eventService.getTop5();

        call.enqueue(new SimpleCallback<>(
                response -> {
                    events.clear();
                    if (response.body() != null)
                        events.addAll(response.body());
                    progressContainerEvents.setVisibility(View.GONE);
                    eventAdapter.notifyDataSetChanged();
                },
                error -> {}
        ));
    }
    @SuppressLint("NotifyDataSetChanged")
    private void fetchTop5ServiceProducts(){
        Call<List<ServiceProductSummaryDto>> call = ClientUtils.serviceProductService.getTop5();

        call.enqueue(new SimpleCallback<>(
                response -> {
                    serviceProducts.clear();
                    if (response.body() != null)
                        serviceProducts.addAll(response.body());
                    progressContainerServiceProducts.setVisibility(View.GONE);
                    serviceProductAdapter.notifyDataSetChanged();
                },
                error -> {}
        ));
    }
    private void onClick(long id) {
        Toast.makeText(getContext(), "id: " + id, Toast.LENGTH_LONG).show();
    }
}