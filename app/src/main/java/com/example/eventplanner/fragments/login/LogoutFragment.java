package com.example.eventplanner.fragments.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.activities.LoginActivity;
import com.example.eventplanner.clients.utils.JwtUtils;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.model.utils.UserRole;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JwtUtils.clearJwtToken(getActivity());
        UserIdUtils.clearUserId(getActivity());
        UserRoleUtils.clearUserRole(getActivity());

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}