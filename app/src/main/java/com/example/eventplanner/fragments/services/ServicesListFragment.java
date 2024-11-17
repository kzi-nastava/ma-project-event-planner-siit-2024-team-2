package com.example.eventplanner.fragments.services;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.adapters.ServiceListAdapter;
import com.example.eventplanner.databinding.FragmentServicesListBinding;
import com.example.eventplanner.model.Service;

import java.util.ArrayList;

public class ServicesListFragment extends ListFragment {
    private ServiceListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<Service> mServices;
    private FragmentServicesListBinding binding;

    public static ServicesListFragment newInstance(ArrayList<Service> services){
        ServicesListFragment fragment = new ServicesListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM, services);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ShopApp", "onCreate Services List Fragment");
        if (getArguments() != null) {
            mServices = getArguments().getParcelableArrayList(ARG_PARAM);
            adapter = new ServiceListAdapter(getActivity(), mServices);
            setListAdapter(adapter);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Services List Fragment");
        binding = FragmentServicesListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}