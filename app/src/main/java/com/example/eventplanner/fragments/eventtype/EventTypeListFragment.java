package com.example.eventplanner.fragments.eventtype;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventTypeAdapter;
import com.example.eventplanner.clients.repositories.event.EventTypeRepository;
import com.example.eventplanner.clients.services.event.EventTypeService;
import com.example.eventplanner.dto.EventTypeDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EventTypeListFragment extends Fragment {

    private EventTypeListViewModel viewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private EventTypeAdapter adapter;
    private List<EventType> eventTypes = new ArrayList<>();
    private EventTypeRepository eventTypeRepository = new EventTypeRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_type_list, container, false);
        viewModel = new ViewModelProvider(this).get(EventTypeListViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_event_types);
        fabAdd = view.findViewById(R.id.fab_add_event_type);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventTypeAdapter(eventTypes, new EventTypeAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(EventType dto) {
                Bundle args = new Bundle();
                args.putLong("id", dto.getId());
                Toast.makeText(getContext(), "Editing event type: " + dto.getName(), Toast.LENGTH_LONG).show();
                //NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                //navController.navigate(R.id.fragment_create_edit_event_type, args);
            }

            @Override
            public void onDeleteClick(EventType dto) {
                deleteEventType(dto.getId());
            }
        });
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            //NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            //navController.navigate(R.id.fragment_create_edit_event_type);
        });

        loadEventTypes();

        return view;
    }

    private void loadEventTypes() {
        // observe LiveData exposed by ViewModel
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), types -> {
            eventTypes.clear();

            if (types != null && !types.isEmpty()) {
                // NOTE: types is List<EventType> (model). If your adapter expects EventTypeDto,
                // convert/map here. Example below assumes adapter accepts EventType model.
                eventTypes.addAll(types);
            }

            adapter.notifyDataSetChanged();
        });

        // trigger load (this will fetch from the repository)
        viewModel.loadEventTypes();
    }

    private void deleteEventType(long id) {
        viewModel.deleteEventType(id).observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Event type deleted", Toast.LENGTH_SHORT).show();
                // reload list
                viewModel.loadEventTypes();
            } else {
                Toast.makeText(requireContext(), "Failed to delete event type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
