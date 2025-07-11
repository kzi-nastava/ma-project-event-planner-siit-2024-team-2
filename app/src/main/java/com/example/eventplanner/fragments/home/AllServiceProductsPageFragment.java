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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ServiceProductAdapter;
import com.example.eventplanner.clients.ClientUtils;
import com.example.eventplanner.databinding.FragmentAllServiceProductsPageBinding;
import com.example.eventplanner.dto.serviceproduct.ServiceProductFilteringValuesDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;
import com.example.eventplanner.model.utils.City;
import com.example.eventplanner.model.utils.PageMetadata;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.model.utils.ServiceProductDType;
import com.example.eventplanner.model.utils.SortDirection;
import com.example.eventplanner.pagination.Pagination;
import com.example.eventplanner.utils.DialogHelper;
import com.example.eventplanner.utils.JsonUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.progressindicator.CircularProgressIndicator;
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

public class AllServiceProductsPageFragment extends Fragment {
    public static List<ServiceProductSummaryDto> serviceProducts = new ArrayList<>();
    public static PageMetadata pageMetadata;
    private AllServiceProductsViewModel viewModel;
    private static final Gson gson = new Gson();
    private FragmentAllServiceProductsPageBinding binding;
    private Spinner spinnerSort;
    int lastSpinnerSelection;
    int currentSelectedIndex;
    private RecyclerView recyclerView;
    private ServiceProductAdapter adapter;
    private BottomSheetDialog bottomSheetDialog;
    private Pagination pagination;
    private CircularProgressIndicator progressIndicator;
    private int currentPage = 1;
    private static final int pageSize = 10;
    //Filtering
    private ServiceProductFilteringValuesDto filteringValues;
    private boolean[] selectedCategories, selectedAvailableEventTypes;
    private List<Long> categories = new ArrayList<>(), eventTypes = new ArrayList<>();
    private Integer minPrice, maxPrice;
    private Float minDuration, maxDuration;
    private Boolean automaticReservation;
    private ServiceProductDType spType;

    public static AllServiceProductsPageFragment newInstance() {
        return new AllServiceProductsPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(AllServiceProductsViewModel.class);

        binding = FragmentAllServiceProductsPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SearchView searchView = binding.searchText;
        viewModel.getHint().observe(getViewLifecycleOwner(), searchView::setQueryHint);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchText(query);
                fetchServiceProducts();
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
        viewModel.getSearchText().observe(getViewLifecycleOwner(), text -> fetchServiceProducts());


        recyclerView = binding.recyclerViewServiceProducts;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new ServiceProductAdapter(serviceProducts);
        recyclerView.setAdapter(adapter);

        progressIndicator = binding.progressServiceProducts;

        Button filter = binding.btnFilter;
        filter.setEnabled(false); // wait until serviceProduct types load
        fetchFilteringValues();
        filter.setOnClickListener(v -> {
            if (bottomSheetDialog == null) {
                bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.FullScreenBottomSheetDialog);
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet_service_product_filter, null);

                MaterialButtonToggleGroup toggleGroup = dialogView.findViewById(R.id.button_toggle_group);
                int toggleServiceId = R.id.button_toggle_service;
                int toggleProductId = R.id.button_toggle_product;
                LinearLayout linearServiceFilter = dialogView.findViewById(R.id.linear_service_filters);
                linearServiceFilter.setVisibility(View.GONE);

                toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                    if (checkedId == toggleServiceId)
                        linearServiceFilter.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                    // There are currently no exclusive product filters
                });

                List<ServiceProductCategory> tempCategories = filteringValues.getCategories();
                String[] categoryNames = new String[tempCategories.size()];
                for (int i = 0; i < tempCategories.size(); ++i)
                    categoryNames[i] = tempCategories.get(i).getName();
                Button categoriesButton = dialogView.findViewById(R.id.button_select_categories);
                TextView summaryCategories = dialogView.findViewById(R.id.text_selected_categories);
                ImageView clearCategories = dialogView.findViewById(R.id.button_clear_categories);
                selectedCategories = new boolean[categoryNames.length];
                DialogHelper.createMultiSelectDialog(categoryNames, categoriesButton, dialogView.getContext(),
                        selectedCategories, "Select Categories:", summaryCategories, clearCategories);
                
                RangeSlider priceSlider = dialogView.findViewById(R.id.range_slider_price);
                Pair<Float, Float> priceRange;
                if (filteringValues == null || Objects.equals(filteringValues.getMinPrice(), filteringValues.getMaxPrice())) {
                    priceRange = new Pair<>(0f, 1f);
                    priceSlider.setEnabled(false);
                } else
                    priceRange = new Pair<>(
                            (float)Math.floor(filteringValues.getMinPrice()),
                            (float)Math.ceil(filteringValues.getMaxPrice()));
                priceSlider.setValueFrom(priceRange.first);
                priceSlider.setValueTo(priceRange.second);
                priceSlider.setValues(priceRange.first, priceRange.second);

                List<EventType> tempTypes = filteringValues.getAvailableEventTypes();
                String[] typeNames = new String[tempTypes.size()];
                for (int i = 0; i < tempTypes.size(); ++i)
                    typeNames[i] = tempTypes.get(i).getName();
                Button typesButton = dialogView.findViewById(R.id.button_select_available_event_types);
                TextView summaryTypes = dialogView.findViewById(R.id.text_selected_available_event_types);
                ImageView clearTypes = dialogView.findViewById(R.id.button_clear_available_event_types);
                selectedAvailableEventTypes = new boolean[typeNames.length];
                DialogHelper.createMultiSelectDialog(typeNames, typesButton, dialogView.getContext(),
                        selectedAvailableEventTypes, "Select Available Event Types:", summaryTypes, clearTypes);

                RangeSlider durationSlider = dialogView.findViewById(R.id.range_slider_duration);
                Pair<Float, Float> durationRange;
                if (filteringValues == null || Objects.equals(filteringValues.getMinDuration(), filteringValues.getMaxDuration())) {
                    durationRange = new Pair<>(0f, 1f);
                    durationSlider.setEnabled(false);
                } else
                    durationRange = new Pair<>(
                            filteringValues.getMinDuration(), filteringValues.getMaxDuration());
                durationSlider.setValueFrom(durationRange.first);
                durationSlider.setValueTo(durationRange.second);
                durationSlider.setValues(durationRange.first, durationRange.second);

                MaterialCheckBox checkBox = dialogView.findViewById(R.id.checkbox_automatic_reservation);
                checkBox.addOnCheckedStateChangedListener((button, state) -> {
                    if (state == MaterialCheckBox.STATE_CHECKED)
                        automaticReservation = true;
                    else if (state == MaterialCheckBox.STATE_UNCHECKED)
                        automaticReservation = false;
                    else
                        automaticReservation = null;
                });

                bottomSheetDialog.setOnDismissListener((dialogInterface) -> {
                    if (toggleGroup.getCheckedButtonId() == toggleServiceId)
                        spType = ServiceProductDType.SERVICE;
                    else if (toggleGroup.getCheckedButtonId() == toggleProductId)
                        spType = ServiceProductDType.PRODUCT;
                    else
                        spType = null;
                    categories.clear();
                    for (int i = 0; i < selectedCategories.length; i++)
                        if (selectedCategories[i])
                            categories.add(filteringValues.getCategories().get(i).getId());

                    if (priceSlider.isEnabled()) {
                        minPrice = Math.round(priceSlider.getValues().get(0));
                        maxPrice = Math.round(priceSlider.getValues().get(1));
                    } else {
                        minPrice = null;
                        maxPrice = null;
                    }

                    eventTypes.clear();
                    for (int i = 0; i < selectedAvailableEventTypes.length; i++)
                        if (selectedAvailableEventTypes[i])
                            categories.add(filteringValues.getAvailableEventTypes().get(i).getId());

                    if (durationSlider.isEnabled()) {
                        minDuration = durationSlider.getValues().get(0);
                        maxDuration = durationSlider.getValues().get(1);
                    } else {
                        minDuration = null;
                        maxDuration = null;
                    }

                    fetchServiceProducts();
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
                R.array.service_product_sort_array,
                android.R.layout.simple_spinner_item
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort = binding.spinnerSort;
        spinnerSort.setAdapter(arrayAdapter);

        pagination = new Pagination(getContext(), 0, binding.paginationServiceProducts);
        pagination.setOnPaginateListener(newPage -> {
            currentPage = newPage;
            viewModel.setCurrentPage(currentPage);
            fetchServiceProducts();
        });

        fetchServiceProducts();

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
                        fetchServiceProducts();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No action needed
                }
            });
        }

        if (binding.paginationServiceProducts.getChildCount() == 0) {
            int totalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
            pagination = new Pagination(getContext(), totalPages, binding.paginationServiceProducts);
            pagination.setOnPaginateListener(newPage -> {
                currentPage = newPage;
                fetchServiceProducts();
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

    private void fetchServiceProducts(){
        String sortBy = null, selectedSort;
        SortDirection sortDirection = null;
        if (spinnerSort != null) {
            selectedSort = spinnerSort.getSelectedItem().toString();
            sortBy = selectedSort.substring(0, selectedSort.indexOf(" ")).toLowerCase();
            sortDirection = selectedSort.substring(selectedSort.indexOf(" ") + 1)
                    .equals("descending") ? SortDirection.DESC : SortDirection.ASC;
        }
        Call<PagedModel<ServiceProductSummaryDto>> call = ClientUtils.serviceProductService.getServiceProductSummaries(
                currentPage-1, pageSize, sortBy, sortDirection,
                viewModel.getSearchText().getValue(), null,
                spType, categories, true, true, minPrice,
                maxPrice, eventTypes, null, minDuration, maxDuration, automaticReservation);

        serviceProducts.clear();
        progressIndicator.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged(); // it's will be safer to use data set changed here, and is performance insignificant.

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<PagedModel<ServiceProductSummaryDto>> call, @NonNull Response<PagedModel<ServiceProductSummaryDto>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null)
                    {
                        serviceProducts.clear();
                        serviceProducts.addAll(response.body().getContent());
                        int previousTotalPages = pageMetadata == null ? 0 : pageMetadata.getTotalPages();
                        pageMetadata = response.body().getPage();
                        if (previousTotalPages != pageMetadata.getTotalPages())
                            pagination.changeTotalPages(pageMetadata.getTotalPages());
                        progressIndicator.setVisibility(View.GONE);
                    }
                    else
                        serviceProducts.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("RetrofitCall", "Failed to fetch serviceProducts. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PagedModel<ServiceProductSummaryDto>> call, @NonNull Throwable t) {
                Log.e("RetrofitCall", Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void fetchFilteringValues() {
        Call<ServiceProductFilteringValuesDto> call = ClientUtils.serviceProductService.getFilteringValues();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ServiceProductFilteringValuesDto> call,
                                   @NonNull Response<ServiceProductFilteringValuesDto> response) {
                if (response.isSuccessful()) {
                    filteringValues = null;
                    if (response.body() != null) {
                        filteringValues = response.body();
                        binding.btnFilter.setEnabled(true); // we can now filter events since everything loaded
                    }
                } else {
                    Log.e("RetrofitCall", "Failed to fetch service product filtering values. Code: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ServiceProductFilteringValuesDto> call, @NonNull Throwable t) {
                Log.e("RetrofitCall", Objects.requireNonNull(t.getMessage()));
            }
        });
    }
}