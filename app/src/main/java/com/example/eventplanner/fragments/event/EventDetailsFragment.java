package com.example.eventplanner.fragments.event;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.repositories.user.UserManagementRepository;
import com.example.eventplanner.clients.utils.AuthUtils;
import com.example.eventplanner.databinding.FragmentAllEventsPageBinding;
import com.example.eventplanner.databinding.FragmentEventDetailsBinding;
import com.example.eventplanner.dialogs.ReportUserDialog;
import com.example.eventplanner.model.event.Event;
import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.user.EventOrganizer;
import com.google.android.material.button.MaterialButton;
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
    private MaterialButton btnReportMenu;
    private MaterialButton btnChatOrganizer;
    private Event currentEvent;
    private UserManagementRepository userManagementRepository;
    private Button btnAttendEvent;
    private boolean isAttending = false;


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
        btnReportMenu = binding.btnReportMenu;
        btnChatOrganizer = binding.btnChatOrganizer;

        progressIndicator = binding.progress;
        progressIndicator.setVisibility(View.VISIBLE);

        detailsLayout = binding.detailsLayout;
        detailsLayout.setVisibility(View.GONE);

        userManagementRepository = new UserManagementRepository();
        btnAttendEvent = binding.btnAttendEvent;

        // Setup report menu
        setupReportMenu();
        
        // Setup chat button
        setupChatButton();

        // ViewModel
        viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        viewModel.getEventById(eventId).observe(getViewLifecycleOwner(), this::populateEvent);
        setupAttendButton();
        return root;
    }

    private void populateEvent(Event event) {
        if (event == null) return;

        currentEvent = event;

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

    private void setupReportMenu() {
        btnReportMenu.setOnClickListener(v -> {
            if (currentEvent == null || currentEvent.getEventOrganizerDto() == null) {
                Toast.makeText(getContext(), "Event organizer information not available", Toast.LENGTH_SHORT).show();
                return;
            }

            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.report_menu, popupMenu.getMenu());
            
            // Hide suspend option if user is not admin
            if (!AuthUtils.isAdmin(getContext())) {
                popupMenu.getMenu().findItem(R.id.action_suspend_user).setVisible(false);
            }

            popupMenu.setOnMenuItemClickListener(this::onReportMenuItemClick);
            popupMenu.show();
        });
    }

    private boolean onReportMenuItemClick(MenuItem item) {
        if (currentEvent == null || currentEvent.getEventOrganizerDto() == null) {
            return false;
        }

        int itemId = item.getItemId();
        if (itemId == R.id.action_report_user) {
            openReportDialog();
            return true;
        } else if (itemId == R.id.action_suspend_user) {
            suspendUser();
            return true;
        }
        return false;
    }

    private void openReportDialog() {
        if (currentEvent == null || currentEvent.getEventOrganizerDto() == null) {
            return;
        }

        BaseUser eo = currentEvent.getEventOrganizerDto();
        String email = currentEvent.getEventOrganizerDto().getEmail();
        String firstName = eo.getFirstName() == null ? "" : eo.getFirstName();
        String lastName = eo.getLastName() == null ? "" : eo.getLastName();
        String name = firstName.isEmpty() ? email : firstName + " " + lastName;

        ReportUserDialog dialog = ReportUserDialog.newInstance(email, name);
        dialog.show(getParentFragmentManager(), "ReportUserDialog");
    }

    private void suspendUser() {
        if (currentEvent == null || currentEvent.getEventOrganizerDto() == null) {
            return;
        }

        String email = currentEvent.getEventOrganizerDto().getEmail();
        userManagementRepository.suspendUser(email).observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), R.string.suspend_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.suspend_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupChatButton() {
        btnChatOrganizer.setOnClickListener(v -> {
            if (currentEvent == null || currentEvent.getEventOrganizerDto() == null) {
                Toast.makeText(getContext(), "Event organizer information not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to chat with organizer
            NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
            Bundle args = new Bundle();
            args.putLong("userId", currentEvent.getEventOrganizerDto().getId());

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.nav_chat, true)
                    .build();

            navController.navigate(R.id.nav_chat, args, navOptions);
        });
    }

    private void setupAttendButton() {
        // Initially disable until event is loaded
        btnAttendEvent.setEnabled(false);
        if (!AuthUtils.isLoggedIn(requireContext())) return;
        // Observe attendance state when event is loaded
        viewModel.isUserAttending(eventId).observe(getViewLifecycleOwner(), attending -> {
            isAttending = Boolean.TRUE.equals(attending);
            updateAttendButtonText();
            btnAttendEvent.setEnabled(true);
        });

        btnAttendEvent.setOnClickListener(v -> {
            btnAttendEvent.setEnabled(false);
            if (isAttending) {
                viewModel.removeAttendance(eventId).observe(getViewLifecycleOwner(), success -> {
                    btnAttendEvent.setEnabled(true);
                    if (Boolean.TRUE.equals(success)) {
                        isAttending = false;
                        updateAttendButtonText();
                        Toast.makeText(getContext(), "You left the event.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to leave event.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                viewModel.attendEvent(eventId).observe(getViewLifecycleOwner(), success -> {
                    btnAttendEvent.setEnabled(true);
                    if (Boolean.TRUE.equals(success)) {
                        isAttending = true;
                        updateAttendButtonText();
                        Toast.makeText(getContext(), "You are now attending this event!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to attend event.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateAttendButtonText() {
        btnAttendEvent.setText(isAttending ? "Cancel Attendance" : "Attend Event");
    }

}
