package com.example.eventplanner.fragments.services;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ProductAdapter;
import com.example.eventplanner.dto.serviceproduct.ProductDto;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductListViewModel viewModel;
    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_list, container, false);
        rvProducts = v.findViewById(R.id.rv_products);
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);

        adapter = new ProductAdapter(new ArrayList<>(), new ProductAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(ProductDto product) {
                Bundle args = new Bundle();
                args.putSerializable("product", product);

                // Navigate using NavController from a view inside the fragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.nav_fragment_create_product, args);
            }

            @Override
            public void onDeleteClick(ProductDto product) {
                showDeleteDialog(product);
            }
        });

        rvProducts.setAdapter(adapter);

        observeData();
        viewModel.loadProducts();

        return v;
    }

    private void observeData() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.notifyDataSetChanged();
            adapter = new ProductAdapter(products, adapter.listener);
            rvProducts.setAdapter(adapter);
        });

        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(requireContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                viewModel.loadProducts();
            }
        });
    }

    private void showDeleteDialog(ProductDto product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete \"" + product.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteProduct(product.getId()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
