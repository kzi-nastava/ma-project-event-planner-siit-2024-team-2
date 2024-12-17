package com.example.eventplanner.activities;

import android.content.Intent;
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
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class SelectLocationActivity extends AppCompatActivity {

    private MapView mapView;
    private GeoPoint selectedLocation;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_location);

        // Initialize osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setUserAgentValue("Mozilla/5.0 (Linux; Android 10; Mobile)");


        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(17.0);
        mapView.getController().setCenter(new GeoPoint(45.246242, 19.845134));

        // Handle map clicks
        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint point) {
                // Update the selected location
                selectedLocation = point;

                // Add or update the marker
                if (currentMarker == null) {
                    currentMarker = new Marker(mapView);
                    mapView.getOverlays().add(currentMarker);
                }
                currentMarker.setPosition(point);
                currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                currentMarker.setTitle("Selected Location");
                mapView.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint point) {
                return false;
            }
        }));


        MaterialButton selectLocationButton = findViewById(R.id.select_location_btn);
        selectLocationButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedLocation.getLatitude());
                resultIntent.putExtra("longitude", selectedLocation.getLongitude());
                setResult(RESULT_OK, resultIntent);
                finish();
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
}
