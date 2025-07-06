package com.example.eventplanner.fragments.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.EventAdapter;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.databinding.FragmentAllEventsPageBinding;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.utils.City;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.pagination.Pagination;
import com.example.eventplanner.utils.JsonUtils;
import com.example.eventplanner.utils.MultiSelectDialogHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllEventsPageFragment extends Fragment {
    public static List<EventSummaryDto> events = new ArrayList<>();
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
    private int currentPage = 1;
    private static final int pageSize = 10;
    //Filtering
    private boolean[] selectedTypes, selectedCities;
    private List<Double> latitudes, longitudes;
    private List<Long> types;
    private Integer minMaxAttendances, maxMaxAttendances;
    private Double maxDistance;
    private Long date;

    public static AllEventsPageFragment newInstance() {
        return new AllEventsPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AllEventsViewModel.class);

        binding = FragmentAllEventsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fetchData();

        SearchView searchView = binding.searchText;
        viewModel.getHint().observe(getViewLifecycleOwner(), searchView::setQueryHint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchText(query);
                fetchData();
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
        viewModel.getSearchText().observe(getViewLifecycleOwner(), text -> fetchData());


        recyclerView = binding.recycleViewEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new EventAdapter(events);
        recyclerView.setAdapter(adapter);


        Button filter = binding.btnFilter;
        filter.setOnClickListener(v -> {
            if (bottomSheetDialog == null)
            {
                bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.FullScreenBottomSheetDialog);
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);

                RangeSlider rangeSlider = dialogView.findViewById(R.id.range_slider_events);
                Pair<Integer, Integer> range = findAttendancesRange();
                rangeSlider.setValueFrom(range.first);
                rangeSlider.setValueTo(range.second);
                if (range.first == 0 && range.second == 1)
                {
                    rangeSlider.setEnabled(false);
                    rangeSlider.setValues(0f, 1f);
                }

                // TODO: Use fetched event types
                EventType[] eventTypes = new EventType[]{
                        new EventType(0L, "Sajam", null),
                        new EventType(1L, "Žurka", null),
                        new EventType(2L, "Proslava", null),
                        new EventType(3L, "Venčanje", null),
                        new EventType(4L, "Rođendan", null)
                };
                String[] typeNames = new String[eventTypes.length];
                for (int i = 0; i < eventTypes.length; ++i)
                    typeNames[i] = eventTypes[i].getName();
                Button typesButton = dialogView.findViewById(R.id.button_select_event_types);
                TextView summaryTypes = dialogView.findViewById(R.id.text_selected_event_types);
                selectedTypes = new boolean[typeNames.length];
                MultiSelectDialogHelper.createDialog(typeNames, typesButton, dialogView.getContext(),
                        selectedTypes, "Select Event Types:", summaryTypes);

                City[] cities = loadCities();
                String[] cityNames = new String[cities.length];
                for (int i = 0; i < cities.length; ++i)
                    cityNames[i] = cities[i].getCity();
                Button citiesButton = dialogView.findViewById(R.id.button_select_locations);
                TextView summaryCities = dialogView.findViewById(R.id.text_selected_locations);
                selectedCities = new boolean[cityNames.length];
                MultiSelectDialogHelper.createDialog(cityNames, citiesButton, dialogView.getContext(),
                        selectedCities, "Select Locations:", summaryCities);

                CheckBox dateCheckBox = dialogView.findViewById(R.id.checkbox_event_use_date);
                CalendarView calendarView = dialogView.findViewById(R.id.calendar_select_event_date);
                calendarView.setVisibility(View.GONE);
                dateCheckBox.setOnCheckedChangeListener((btn, checked) ->
                        calendarView.setVisibility(checked ? View.VISIBLE : View.GONE));

                bottomSheetDialog.setOnDismissListener((dialogInterface) -> {
                    types = new ArrayList<>();
                    for (int i = 0; i < selectedTypes.length; i++)
                        if (selectedTypes[i])
                            types.add(eventTypes[i].getId());

                    if (rangeSlider.isEnabled())
                    {
                        minMaxAttendances = Math.round(rangeSlider.getValues().get(0));
                        maxMaxAttendances = Math.round(rangeSlider.getValues().get(1));
                    }
                    else
                    {
                        minMaxAttendances = null;
                        maxMaxAttendances = null;
                    }

                    latitudes = new ArrayList<>();
                    longitudes = new ArrayList<>();
                    for (int i = 0; i < selectedCities.length; i++)
                        if (selectedCities[i])
                        {
                            latitudes.add(cities[i].getLat());
                            longitudes.add(cities[i].getLng());
                        }

                    if (latitudes != null && !latitudes.isEmpty())
                    {
                        Slider slider = dialogView.findViewById(R.id.slider_distance_events);
                        maxDistance = (double) slider.getValue();
                    }
                    else
                        maxDistance = null;

                    if (dateCheckBox.isChecked())
                        date = calendarView.getDate();
                    else
                        date = null;

                    fetchData();
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
            fetchData();
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i("Resume","Resume");

        spinnerSort.setSelection(spinnerSort.getSelectedItemPosition(), false);
        if (spinnerSort.getOnItemSelectedListener() == null) {
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentSelectedIndex = spinnerSort.getSelectedItemPosition();
                    if (currentSelectedIndex != lastSpinnerSelection) {
                        Log.v("EventPlanner", (String) parent.getItemAtPosition(position));
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                        lastSpinnerSelection = currentSelectedIndex;
                        currentSelectedIndex = position;
                        fetchData();
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
                fetchData();
            });

            currentPage = viewModel.getCurrentPage();
            pagination.toPage(currentPage);
        }
        //FragmentTransition.to(AllEventsListFragment.newInstance(events), requireActivity(), false, R.id.scroll_products_list);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchData(){
        String sortBy = null, selectedSort;
        SortDirection sortDirection = null;
        if (spinnerSort != null) {
            selectedSort = spinnerSort.getSelectedItem().toString();
            sortBy = selectedSort.substring(0, selectedSort.indexOf(" ")).toLowerCase();
            sortDirection = selectedSort.substring(selectedSort.indexOf(" ") + 1)
                    .equals("descending") ? SortDirection.DESC : SortDirection.ASC;
        }
        Call<PagedModel<EventSummaryDto>> call = ClientUtils.eventService.getEventSummaries(
                currentPage-1, pageSize, sortBy, sortDirection,
                viewModel.getSearchText().getValue(), null,
                types, minMaxAttendances, maxMaxAttendances, true, latitudes,
                longitudes, maxDistance, date, date);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<PagedModel<EventSummaryDto>> call, @NonNull Response<PagedModel<EventSummaryDto>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                    {
                        events.clear();
                        events.addAll(response.body().getContent());
                        int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                        pageMetadata = response.body().getPage();
                        if (previousTotalPages != pageMetadata.getTotalPages())
                            pagination.changeTotalPages(pageMetadata.getTotalPages());
                    }
                    else
                        events.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("RetrofitCall", "Failed to fetch events. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagedModel<EventSummaryDto>> call, @NonNull Throwable t) {
                Log.e("RetrofitCall", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private Pair<Integer, Integer> findAttendancesRange() {
        int minValue, maxValue;
        if (events.isEmpty()) {
            minValue = 0;
            maxValue = 100;
        } else {
            minValue = events.get(0).getMaxAttendances();
            maxValue = events.get(0).getMaxAttendances();
        }
        for (EventSummaryDto event : events) {
            minValue = Math.min(minValue, event.getMaxAttendances());
            maxValue = Math.max(maxValue, event.getMaxAttendances());
        }
        if (minValue == maxValue)
            maxValue++;
        return new Pair<>(minValue, maxValue);
    }

    private City[] loadCities()
    {
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