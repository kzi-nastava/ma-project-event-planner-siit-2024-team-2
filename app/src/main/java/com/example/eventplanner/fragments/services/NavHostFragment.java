package com.example.eventplanner.fragments.services;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentNavHostBinding;
import com.example.eventplanner.databinding.FragmentServicesPageBinding;

public class NavHostFragment extends Fragment {

    private FragmentNavHostBinding binding;

    public static NavHostFragment newInstance() {
        return new NavHostFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNavHostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentTransition.to(ServicesPageFragment.newInstance(), getActivity(), false, R.id.all_services_page);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}