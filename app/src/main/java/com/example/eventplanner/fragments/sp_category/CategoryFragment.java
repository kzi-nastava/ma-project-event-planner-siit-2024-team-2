package com.example.eventplanner.fragments.sp_category;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.CategoryAdapter;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ProductRepository;
import com.example.eventplanner.clients.repositories.serviceproduct.ServiceProductCategoryRepository;
import com.example.eventplanner.dto.serviceproduct.ProductDto;
import com.example.eventplanner.fragments.eventtype.EventTypeListViewModel;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.Product;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private CategoryViewModel viewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private CategoryAdapter adapter;
    private List<ServiceProductCategory> categories = new ArrayList<>();
    private ProductRepository productRepository = new ProductRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_categories);
        fabAdd = view.findViewById(R.id.fab_add_category);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(categories, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(ServiceProductCategory dto) {
                Bundle args = new Bundle();
                args.putLong("id", dto.getId());
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.nav_create_category, args);
            }

            @Override
            public void onDeleteClick(ServiceProductCategory dto) {
                productRepository.getAllProducts()
                    .observe(getViewLifecycleOwner(), products -> {
                        boolean canDelete = true;
                        for (ProductDto product : products) {
                            if (product.getCategoryId() == dto.getId()) {
                                // Category has products - show error
                                Toast.makeText(requireContext(),
                                        "Cannot delete. This category has attached product(s).",
                                        Toast.LENGTH_LONG).show();
                                canDelete = false;
                                break;
                            }
                        }
                        if (canDelete) showDeleteConfirmation(dto);
                    });
            }

            private void showDeleteConfirmation(ServiceProductCategory category) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete \"" + category.getName() + "\"?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteCategory(category.getId());
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            navController.navigate(R.id.nav_create_category);
        });

        loadCategories();

        return view;
    }

    private void loadCategories() {
        // observe LiveData exposed by ViewModel
        viewModel.getCategories().observe(getViewLifecycleOwner(), types -> {
            categories.clear();

            if (types != null && !types.isEmpty()) {
                // NOTE: types is List<EventType> (model). If your adapter expects EventTypeDto,
                // convert/map here. Example below assumes adapter accepts EventType model.
                categories.addAll(types);
            }

            adapter.notifyDataSetChanged();
        });

        // trigger load (this will fetch from the repository)
        viewModel.loadCategories();
    }

    private void deleteCategory(long id) {
        viewModel.deleteCategory(id).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                // reload list
                viewModel.loadCategories();
            } else {
                Toast.makeText(requireContext(), "Failed to delete category", Toast.LENGTH_SHORT).show();
            }
        });
    }
}