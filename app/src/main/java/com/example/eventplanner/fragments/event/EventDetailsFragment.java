package com.example.eventplanner.fragments.event;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentAllEventsPageBinding;
import com.example.eventplanner.databinding.FragmentEventDetailsBinding;
import com.example.eventplanner.model.event.Event;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailsFragment extends Fragment {

    private EventDetailsViewModel viewModel;
    private long eventId;

    private TextView tvName, tvDescription, tvMaxAttendances, tvType, tvDate, tvLocation, tvIsOpen;
    private MapView mapView;
    private LinearLayout detailsLayout;
    private FragmentEventDetailsBinding binding;
    private CircularProgressIndicator progressIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong("eventId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEventDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup map
        mapView = binding.mapView;
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx,
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx));

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Bind views
        tvName = binding.tvEventName;
        tvDescription = binding.tvEventDescription;
        tvMaxAttendances = binding.tvEventMaxAttendances;
        tvType = binding.tvEventType;
        tvDate = binding.tvEventDate;
        tvLocation = binding.tvEventLocation;
        tvIsOpen = binding.tvEventIsOpen;

        progressIndicator = binding.progress;
        progressIndicator.setVisibility(View.VISIBLE);

        detailsLayout = binding.detailsLayout;
        detailsLayout.setVisibility(View.GONE);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        viewModel.getEventById(eventId).observe(getViewLifecycleOwner(), this::populateEvent);

        return root;
    }

    private void populateEvent(Event event) {
        if (event == null) return;

        // Fill UI
        tvName.setText(event.getName());
        tvDescription.setText(event.getDescription());
        tvMaxAttendances.setText(String.valueOf(event.getMaxAttendances()));
        tvType.setText(event.getType() != null ? event.getType().getName() : "N/A");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(new Date(event.getDate())));

        tvLocation.setText(String.format(Locale.getDefault(),
                "%.5f, %.5f", event.getLatitude(), event.getLongitude()));
        tvIsOpen.setText(event.isOpen() ? "Open" : "Closed");

        // Update map with event location
        if (event.getLatitude() != 0.0 && event.getLongitude() != 0.0) {
            IMapController mapController = mapView.getController();
            mapController.setZoom(15.0);

            GeoPoint point = new GeoPoint(event.getLatitude(), event.getLongitude());
            mapController.setCenter(point);

            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(event.getName());
            mapView.getOverlays().clear(); // remove old markers if any
            mapView.getOverlays().add(marker);
        } else {
            mapView.setVisibility(View.GONE);
        }

        progressIndicator.setVisibility(View.GONE);
        detailsLayout.setVisibility(View.VISIBLE);
    }
}
