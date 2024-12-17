package com.example.eventplanner.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.eventplanner.R;
import com.google.android.material.button.MaterialButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class SelectLocationActivity extends AppCompatActivity {

    private MapView mapView;
    private GeoPoint selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_location);

        // Initialize osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        mapView = findViewById(R.id.map);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(0.0, 0.0));

        // Handle map clicks
        mapView.setOnClickListener(v -> {
            GeoPoint center = (GeoPoint) mapView.getMapCenter();
            addMarker(center);
        });

        MaterialButton selectLocationButton = findViewById(R.id.select_location_btn);
        selectLocationButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                String locationInfo = "Lat: " + selectedLocation.getLatitude() + ", Lon: " + selectedLocation.getLongitude();
                Toast.makeText(this, "Selected Location: " + locationInfo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "No location selected.", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addMarker(GeoPoint point) {
        selectedLocation = point;
        mapView.getOverlays().clear();
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Selected Location");
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }
}
