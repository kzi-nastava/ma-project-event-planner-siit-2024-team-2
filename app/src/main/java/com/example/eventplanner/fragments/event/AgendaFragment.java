package com.example.eventplanner.fragments.event;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ActivityAdapter;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.databinding.FragmentAgendaBinding;
import com.example.eventplanner.dto.event.ActivityDto;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class AgendaFragment extends Fragment {
    private FragmentAgendaBinding binding;
    private long eventId;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private final List<ActivityDto> activities = new ArrayList<ActivityDto>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAgendaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventId = getArguments() != null ? getArguments().getLong("eventId") : -1;

        recyclerView = binding.recyclerViewActivities;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ActivityAdapter(activities, new ActivityAdapter.OnActivityActionListener() {
            @Override
            public void onEdit(int position, ActivityDto activity) {
                Bundle args = new Bundle();
                args.putLong("eventId", eventId);
                args.putLong("activityId", activity.getId());
                args.putSerializable("activity", activity);
                Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main)
                        .navigate(R.id.editActivityFragment, args);
            }

            @Override
            public void onDelete(int position, ActivityDto activity) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Activity")
                        .setMessage("Are you sure you want to delete this activity?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Call<Void> call = ClientUtils.agendaService.deleteActivity(eventId, activity.getId());
                            call.enqueue(new SimpleCallback<>(
                                    response -> {
                                        adapter.removeAt(position);
                                        Toast.makeText(getContext(), "Activity deleted", Toast.LENGTH_SHORT).show();
                                    },
                                    error -> Toast.makeText(getContext(), "Failed to delete activity", Toast.LENGTH_SHORT).show()
                            ));
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton btnAdd = binding.fabAddActivity;
        btnAdd.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("eventId", eventId);
            Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main)
                    .navigate(R.id.create_activity_fragment, args);
        });

        fetchActivities();
        return root;
    }

    private void fetchActivities() {
        Call<List<ActivityDto>> call = ClientUtils.agendaService.getAgenda(eventId);
        call.enqueue(new SimpleCallback<>(
                response -> {
                    activities.clear();
                    activities.addAll(response.body());
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "Failed to load activities", Toast.LENGTH_SHORT).show()
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
