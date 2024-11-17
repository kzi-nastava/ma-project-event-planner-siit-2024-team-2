package com.example.eventplanner.fragments.services;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentServicesPageBinding;
import com.example.eventplanner.model.Service;
import java.util.ArrayList;

public class ServicesPageFragment extends Fragment {

    public static ArrayList<Service> services = new ArrayList<Service>();
    private FragmentServicesPageBinding binding;

    public static ServicesPageFragment newInstance() {
        return new ServicesPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareProductList(services);

        FragmentTransition.to(ServicesListFragment.newInstance(services), getActivity(),
                false, R.id.scroll_services_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareProductList(ArrayList<Service> services){
        services.clear();
        services.add(new Service(1L, "Your catering", "Description 1", R.drawable.catering));
        services.add(new Service(1L, "Your catering", "Description 2", R.drawable.catering));
        services.add(new Service(1L, "Your catering", "Description 3", R.drawable.catering));
    }
}