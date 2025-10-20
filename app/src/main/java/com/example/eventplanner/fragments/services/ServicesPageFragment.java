package com.example.eventplanner.fragments.services;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceAdapter;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceRepository;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.dto.serviceproduct.ServiceDto;

import java.util.ArrayList;

public class ServicesPageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private ServicesViewModel viewModel;
    private SearchView searchView;
    private Button btnPriceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services_page, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.search_view);
        btnPriceList = view.findViewById(R.id.btnPrices);

        adapter = new ServiceAdapter(new ArrayList<>(), new ServiceAdapter.OnServiceClickListener() {
            @Override
            public void onEditClick(ServiceDto service) {
                Bundle args = new Bundle();
                args.putSerializable("serviceId", service.getId());

                // Navigate using NavController from a view inside the fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.nav_edit_service, args);
            }

            @Override
            public void onDeleteClick(ServiceDto service) {
                showDeleteDialog(service);
                viewModel.loadServices();
            }
        });
        recyclerView.setAdapter(adapter);

        ServiceRepository repository = new ServiceRepository();
        ServicesViewModelFactory factory = new ServicesViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ServicesViewModel.class);
        viewModel.getServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) adapter.setServices(services);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });

        btnPriceList.setOnClickListener(v -> {
            Bundle args = new Bundle();
            long id = UserIdUtils.getUserId(getContext());
            args.putLong("sppId", id);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            navController.navigate(R.id.nav_price_list, args);
        });

        return view;
    }

    private void showDeleteDialog(ServiceDto service) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete \"" + service.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteService(service.getId());
                    observeDeleteResult();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void observeDeleteResult() {
        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Service deleted successfully!", Toast.LENGTH_SHORT).show();
                viewModel.loadServices();

            } else {
                Toast.makeText(requireContext(), "Failed to delete service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadServices();
    }
}