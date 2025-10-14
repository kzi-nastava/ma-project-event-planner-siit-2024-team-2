package com.example.eventplanner.fragments.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventplanner.databinding.FragmentExampleBinding;
import com.example.eventplanner.databinding.FragmentNotificationsBinding;

public class ExampleFragment extends Fragment {
    private FragmentExampleBinding binding;
    private ExampleViewModel viewModel;

    public ExampleFragment() {
        // Required empty public constructor
    }

    public static ExampleFragment newInstance() {
        return new ExampleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExampleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(ExampleViewModel.class);

        return root;
    }
}