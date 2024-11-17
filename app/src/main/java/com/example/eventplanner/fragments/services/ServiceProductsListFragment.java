package com.example.eventplanner.fragments.services;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.adapters.ServiceProductsListAdapter;
import com.example.eventplanner.databinding.FragmentServiceProductsListBinding;
import com.example.eventplanner.model.ServiceProduct;

import java.util.ArrayList;

public class ServiceProductsListFragment extends ListFragment {
    private ServiceProductsListAdapter adapter;
    private static final String ARG_PARAM = "param";
    private ArrayList<ServiceProduct> mServiceProducts;
    private FragmentServiceProductsListBinding binding;

    public static ServiceProductsListFragment newInstance(ArrayList<ServiceProduct> services){
        ServiceProductsListFragment fragment = new ServiceProductsListFragment();
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
            mServiceProducts = getArguments().getParcelableArrayList(ARG_PARAM);
            adapter = new ServiceProductsListAdapter(getActivity(), mServiceProducts);
            setListAdapter(adapter);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("ShopApp", "onCreateView Services List Fragment");
        binding = FragmentServiceProductsListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}