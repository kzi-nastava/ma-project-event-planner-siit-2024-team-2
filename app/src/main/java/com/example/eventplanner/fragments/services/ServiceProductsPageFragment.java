package com.example.eventplanner.fragments.services;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentServiceProductsPageBinding;
import com.example.eventplanner.model.ServiceProduct;
import java.util.ArrayList;

public class ServiceProductsPageFragment extends Fragment {

    public static ArrayList<ServiceProduct> serviceProducts = new ArrayList<ServiceProduct>();
    private FragmentServiceProductsPageBinding binding;

    public static ServiceProductsPageFragment newInstance() {
        return new ServiceProductsPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServiceProductsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareProductList(serviceProducts);

        FragmentTransition.to(ServiceProductsListFragment.newInstance(serviceProducts), getActivity(),
                false, R.id.scroll_service_products_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareProductList(ArrayList<ServiceProduct> services){
        services.clear();
        services.add(new ServiceProduct(1L, "Your catering", "We offer a brilliant service of catering. Don't worry because we're coming while your meal is still hot! Lorem ipsum  tra la la...", R.drawable.catering));
        services.add(new ServiceProduct(2L, "Our catering", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", R.drawable.catering));
        services.add(new ServiceProduct(3L, "My catering", "We offer a brilliant service of catering. Don't worry because we're coming while your meal is still hot! Lorem ipsum  tra la la...", R.drawable.catering));
    }
}