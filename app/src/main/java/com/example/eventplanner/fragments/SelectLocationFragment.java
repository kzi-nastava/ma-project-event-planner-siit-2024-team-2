package com.example.eventplanner.fragments;

import static androidx.fragment.app.FragmentKt.setFragmentResult;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventplanner.R;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class SelectLocationFragment extends Fragment {

    private MapView mapView;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location, container, false);
        mapView = view.findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(new GeoPoint(0.0, 0.0));

        // Handle map click for location selection
        mapView.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                IGeoPoint clickedPoint = mapView.getProjection().fromPixels(
                        (int) motionEvent.getX(), (int) motionEvent.getY());
                double latitude = clickedPoint.getLatitude();
                double longitude = clickedPoint.getLongitude();
                Toast.makeText(getContext(), "Location Selected: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                Bundle result = new Bundle();
                result.putDouble("latitude", clickedPoint.getLatitude());
                result.putDouble("longitude", clickedPoint.getLongitude());

                setFragmentResult(this, "locationResult", result);
                getParentFragmentManager().popBackStack();
            }
            return false;
        });
        return view;
    }
}