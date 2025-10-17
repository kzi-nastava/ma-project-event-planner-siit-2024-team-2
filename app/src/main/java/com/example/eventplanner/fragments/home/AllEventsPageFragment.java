package com.example.eventplanner.fragments.home;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventAdapter;
import com.example.eventplanner.clients.utils.ClientUtils;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.databinding.FragmentAllEventsPageBinding;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.utils.City;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.pagination.Pagination;
import com.example.eventplanner.utils.JsonUtils;
import com.example.eventplanner.utils.DialogHelper;
import com.example.eventplanner.utils.SimpleCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;

public class AllEventsPageFragment extends Fragment {
    public static List<EventSummaryDto> events = new ArrayList<>();
    public static List<EventType> eventTypes = new ArrayList<>();
    public static PageMetadata pageMetadata;
    private AllEventsViewModel viewModel;
    private static final Gson gson = new Gson();
    private FragmentAllEventsPageBinding binding;
    private Spinner spinnerSort;
    int lastSpinnerSelection;
    int currentSelectedIndex;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;
    private Pagination pagination;
    private CircularProgressIndicator progressIndicator;
    private int currentPage = 1;
    private static final int pageSize = 10;
    //Filtering
    private boolean[] selectedTypes, selectedCities;
    private List<Double> latitudes = new ArrayList<>(), longitudes = new ArrayList<>();
    private List<Long> types = new ArrayList<>(), dateRange = new ArrayList<>();
    private List<Integer> fullMaxAttendancesRange = new ArrayList<>();
    private Integer minMaxAttendances, maxMaxAttendances;
    private Double maxDistance;
    private Long minDate, maxDate;
    private boolean fetchedTypes, fetchedMaxAttendances;

    public static AllEventsPageFragment newInstance() {
        return new AllEventsPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AllEventsViewModel.class);

        binding = FragmentAllEventsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SearchView searchView = binding.searchText;
        viewModel.getHint().observe(getViewLifecycleOwner(), searchView::setQueryHint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchText(query);
                fetchEventsWithFilters();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isBlank()) {
                    viewModel.setSearchText(newText);
                    return true;
                }
                return false;
            }
        });
        viewModel.getSearchText().observe(getViewLifecycleOwner(), text -> fetchEventsWithFilters());


        recyclerView = binding.recyclerViewEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new EventAdapter(events, new EventAdapter.OnEventClickListener() {
            @Override
            public void onMoreInfoClick(EventSummaryDto event) {
                EventDetailsFragment detailsFragment = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putLong("eventId", event.getId());
                detailsFragment.setArguments(args);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.action_all_events_page_to_event_details, args);
            }

            @Override
            public void onHeartClick(EventSummaryDto event, boolean isFavorite) {
                long userId = UserIdUtils.getUserId(getContext());
                if (isFavorite) {
                    viewModel.addFavoriteEvent(userId, event.getId());
                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.removeFavoriteEvent(userId, event.getId());
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                }
            }

        });
        recyclerView.setAdapter(adapter);
        viewModel.getFavoriteEventIds().observe(getViewLifecycleOwner(), favIds -> {
            for (EventSummaryDto e : events) {
                e.setFavorite(favIds.contains(e.getId()));
            }
            adapter.notifyDataSetChanged();
        });
        viewModel.getEvents().observe(getViewLifecycleOwner(), response -> {
            events.clear();
            if (response != null) {
                events.addAll(response.getContent());

                // Mark favorites using ViewModel
                List<Long> favIds = viewModel.getFavoriteEventIds().getValue();
                if (favIds != null) {
                    for (EventSummaryDto e : events) {
                        e.setFavorite(favIds.contains(e.getId()));
                    }
                }

                int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                pageMetadata = response.getPage();
                if (previousTotalPages != pageMetadata.getTotalPages())
                    pagination.changeTotalPages(pageMetadata.getTotalPages());
            }
            progressIndicator.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        });
        viewModel.getEventTypes().observe(getViewLifecycleOwner(), response -> {
            eventTypes.clear();
            if (response != null) {
                eventTypes.addAll(response);
                eventTypes.sort(Comparator.comparing(EventType::getName));
                fetchedTypes = true;
                if (fetchedMaxAttendances)
                    binding.btnFilter.setEnabled(true); // we can now filter events since everything loaded
            }
        });
        viewModel.getMaxAttendancesRange().observe(getViewLifecycleOwner(), response -> {
            fullMaxAttendancesRange.clear();
            if (response != null) {
                fullMaxAttendancesRange.addAll(response);
                fetchedMaxAttendances = true;
                if (fetchedTypes)
                    binding.btnFilter.setEnabled(true); // we can now filter events since everything loaded
            }
        });

        progressIndicator = binding.progressEvents;

        Button filter = binding.btnFilter;
        filter.setEnabled(false); // wait until event types load
        viewModel.fetchEventTypes();
        viewModel.fetchAttendancesRange();
        filter.setOnClickListener(v -> {
            if (bottomSheetDialog == null) {
                bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.FullScreenBottomSheetDialog);
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_event_filter, null);

                RangeSlider rangeSlider = dialogView.findViewById(R.id.range_slider_events);
                Pair<Integer, Integer> range;
                if (fullMaxAttendancesRange == null || fullMaxAttendancesRange.size() < 2)
                    range = new Pair<>(0, 1);
                else if (Objects.equals(fullMaxAttendancesRange.get(0), fullMaxAttendancesRange.get(1)))
                    range = new Pair<>(0, 1);
                else
                    range = new Pair<>(fullMaxAttendancesRange.get(0), fullMaxAttendancesRange.get(1));
                rangeSlider.setValueFrom(range.first);
                rangeSlider.setValueTo(range.second);
                rangeSlider.setValues((float)range.first, (float)range.second);
                if (range.first == 0 && range.second == 1)
                    rangeSlider.setEnabled(false);

                String[] typeNames = new String[eventTypes.size()];
                for (int i = 0; i < eventTypes.size(); ++i)
                    typeNames[i] = eventTypes.get(i).getName();
                Button typesButton = dialogView.findViewById(R.id.button_select_event_types);
                TextView summaryTypes = dialogView.findViewById(R.id.text_selected_event_types);
                ImageView clearTypes = dialogView.findViewById(R.id.button_clear_event_types);
                selectedTypes = new boolean[typeNames.length];
                DialogHelper.createMultiSelectDialog(typeNames, typesButton, dialogView.getContext(),
                        selectedTypes, "Select Event Types:", summaryTypes, clearTypes);

                City[] cities = loadCities();
                String[] cityNames = new String[cities.length];
                for (int i = 0; i < cities.length; ++i)
                    cityNames[i] = cities[i].getCity();
                Button citiesButton = dialogView.findViewById(R.id.button_select_locations);
                TextView summaryCities = dialogView.findViewById(R.id.text_selected_locations);
                ImageView clearCities = dialogView.findViewById(R.id.button_clear_locations);
                selectedCities = new boolean[cityNames.length];
                DialogHelper.createMultiSelectDialog(cityNames, citiesButton, dialogView.getContext(),
                        selectedCities, "Select Locations:", summaryCities, clearCities);

                Button datesButton = dialogView.findViewById(R.id.button_select_dates);
                TextView summaryDates = dialogView.findViewById(R.id.text_selected_dates);
                ImageView clearDates = dialogView.findViewById(R.id.button_clear_dates);
                DialogHelper.createDateRangeDialog(datesButton, "Select a date range", summaryDates,
                        dateRange, getChildFragmentManager(), clearDates);

                bottomSheetDialog.setOnDismissListener((dialogInterface) -> {
                    types.clear();
                    for (int i = 0; i < selectedTypes.length; i++)
                        if (selectedTypes[i])
                            types.add(eventTypes.get(i).getId());

                    if (rangeSlider.isEnabled()) {
                        minMaxAttendances = Math.round(rangeSlider.getValues().get(0));
                        maxMaxAttendances = Math.round(rangeSlider.getValues().get(1));
                    } else {
                        minMaxAttendances = null;
                        maxMaxAttendances = null;
                    }

                    latitudes.clear();
                    longitudes.clear();
                    for (int i = 0; i < selectedCities.length; i++)
                        if (selectedCities[i]) {
                            latitudes.add(cities[i].getLat());
                            longitudes.add(cities[i].getLng());
                        }

                    if (latitudes != null && !latitudes.isEmpty()) {
                        Slider slider = dialogView.findViewById(R.id.slider_distance_events);
                        maxDistance = (double) slider.getValue();
                    } else
                        maxDistance = null;

                    if (dateRange != null && dateRange.size() >= 2) {
                        minDate = dateRange.get(0);
                        maxDate = dateRange.get(1);
                    } else {
                        minDate = null;
                        maxDate = null;
                    }

                    fetchEventsWithFilters();
                });

                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
            }
            else {
                bottomSheetDialog.show();
            }
        });
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.event_sort_array,
                android.R.layout.simple_spinner_item
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort = binding.spinnerSort;
        spinnerSort.setAdapter(arrayAdapter);

        pagination = new Pagination(getContext(), 0, binding.paginationEvents);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchEventsWithFilters();
        });

        fetchEventsWithFilters();

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();

        spinnerSort.setSelection(spinnerSort.getSelectedItemPosition(), false);
        if (spinnerSort.getOnItemSelectedListener() == null) {
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentSelectedIndex = spinnerSort.getSelectedItemPosition();
                    if (currentSelectedIndex != lastSpinnerSelection) {
                        lastSpinnerSelection = currentSelectedIndex;
                        currentSelectedIndex = position;
                        fetchEventsWithFilters();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action needed
                }
            });
        }

        if (binding.paginationEvents.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationEvents);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchEventsWithFilters();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchEventsWithFilters() {
        String sortBy = null, selectedSort;
        SortDirection sortDirection = null;
        if (spinnerSort != null) {
            selectedSort = spinnerSort.getSelectedItem().toString();
            sortBy = selectedSort.substring(0, selectedSort.indexOf(" ")).toLowerCase();
            sortDirection = selectedSort.substring(selectedSort.indexOf(" ") + 1)
                    .equals("descending") ? SortDirection.DESC : SortDirection.ASC;
        }

        events.clear();
        adapter.notifyDataSetChanged();
        progressIndicator.setVisibility(View.VISIBLE);

        viewModel.fetchEvents(
                currentPage - 1, pageSize, sortBy, sortDirection,
                viewModel.getSearchText().getValue(), null,
                types, minMaxAttendances, maxMaxAttendances, true, latitudes,
                longitudes, maxDistance, minDate, maxDate
        );
    }

    private City[] loadCities() {
        String json;
        try {
            json = JsonUtils.readJsonFromAssets(getContext(), "cities.json");
        } catch (IOException e) {
            json = "[]";
        }
        City[] cities = gson.fromJson(json, City[].class);
        Arrays.sort(cities);
        return cities;
    }
}