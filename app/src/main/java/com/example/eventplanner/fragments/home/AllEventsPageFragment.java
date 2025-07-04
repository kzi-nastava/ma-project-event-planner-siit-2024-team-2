package com.example.eventplanner.fragments.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.pagination.OnPaginateListener;
import com.example.eventplanner.pagination.Pagination;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllEventsPageFragment extends Fragment {
    public static List<EventSummaryDto> events = new ArrayList<>();
    public static PageMetadata pageMetadata;
    private AllEventsViewModel viewModel;
    private FragmentAllEventsPageBinding binding;
    private Spinner spinner;
    int lastSpinnerSelection;
    int currentSelectedIndex;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private Pagination pagination;
    private int currentPage = 1;
    private static final int pageSize = 10;

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
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.FullScreenBottomSheetDialog);
            View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);

            LinearLayout linearLayout = dialogView.findViewById(R.id.linear_event_types);
            // TODO: Use fetched event types
            EventType[] eventTypes = new EventType[]{
                    new EventType(0L, "Sajam", null),
                    new EventType(1L, "Žurka", null),
                    new EventType(2L, "Proslava", null),
                    new EventType(3L, "Venčanje", null),
                    new EventType(4L, "Rođendan", null)
            };
            for (EventType type : eventTypes) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                checkBox.setText(StringUtils.capitalize(type.getName()));
                linearLayout.addView(checkBox);
            }
            RangeSlider rangeSlider = dialogView.findViewById(R.id.range_slider_events);
            Pair<Integer, Integer> range = findAttendancesRange();
            rangeSlider.setValueFrom(range.first);
            rangeSlider.setValueTo(range.second);
            if (range.first == 0 && range.second == 1)
            {
                rangeSlider.setEnabled(false);
                rangeSlider.setValues(0f, 1f);
            }
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
        });
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.event_sort_array,
                android.R.layout.simple_spinner_item
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = binding.spinnerSort;
        spinner.setAdapter(arrayAdapter);

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

        spinner.setSelection(spinner.getSelectedItemPosition(), false);
        if (spinner.getOnItemSelectedListener() == null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentSelectedIndex = spinner.getSelectedItemPosition();
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
        if (spinner != null) {
            selectedSort = spinner.getSelectedItem().toString();
            sortBy = selectedSort.substring(0, selectedSort.indexOf(" ")).toLowerCase();
            sortDirection = selectedSort.substring(selectedSort.indexOf(" ") + 1)
                    .equals("descending") ? SortDirection.DESC : SortDirection.ASC;
        }
        Call<PagedModel<EventSummaryDto>> call = ClientUtils.eventService.getEventSummaries(
                currentPage-1, pageSize, sortBy, sortDirection,
                viewModel.getSearchText().getValue(), null,
                null, null, null, true, null,
                null, null, null ,null);

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
}