package com.example.eventplanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private final Set<Integer> topLevelDestinations = new HashSet<>();

    // Define which routes require authentication
    private final Set<Integer> protectedDestinations = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup views
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Setup Navigation
        navController = Navigation.findNavController(this, R.id.fragment_nav_content_main);

        // Top-level destinations (no "back" arrow)
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_settings);
        topLevelDestinations.add(R.id.nav_language);
        topLevelDestinations.add(R.id.nav_logout);

        // Protected routes — user must be logged in
        protectedDestinations.add(R.id.nav_fragment_create_product);
        protectedDestinations.add(R.id.nav_fragment_my_products);
        protectedDestinations.add(R.id.nav_fragment_create_event);
        protectedDestinations.add(R.id.nav_fragment_profile);

        protectedDestinations.add(R.id.nav_fragment_category);
        protectedDestinations.add(R.id.nav_create_category);

        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawerLayout)
                .build();

        // Drawer toggle
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Setup navigation UI
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // 🔒 Add route protection
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (protectedDestinations.contains(destination.getId()) && UserIdUtils.getUserId(this) < 0) {
                // User is not logged in — redirect to LoginActivity
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        setupMenuVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (NavigationUI.onNavDestinationSelected(item, navController)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setupMenuVisibility() {
        Menu menu = navigationView.getMenu();

        // Example role loading — adapt to your system
        String role = UserRoleUtils.getUserRole(this);

        // Hide everything first (optional)
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }

        // Common for all users
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_logout).setVisible(true);
        menu.findItem(R.id.nav_fragment_profile).setVisible(true);

        if (role == null) return;

        switch (role) {
            case "ADMIN":
                menu.findItem(R.id.nav_event_types).setVisible(true);
                menu.findItem(R.id.nav_create_event_type).setVisible(true);
                menu.findItem(R.id.nav_fragment_category).setVisible(true);
                menu.findItem(R.id.nav_create_category).setVisible(true);
                break;

            case "SERVICE_PRODUCT_PROVIDER":
                menu.findItem(R.id.nav_fragment_create_product).setVisible(true);
                menu.findItem(R.id.nav_fragment_my_products).setVisible(true);
                menu.findItem(R.id.nav_services).setVisible(true);
                menu.findItem(R.id.nav_edit_service).setVisible(true);
                break;

            case "EVENT_ORGANIZER":
                menu.findItem(R.id.nav_fragment_create_event).setVisible(true);
                menu.findItem(R.id.nav_my_events).setVisible(true);
                break;

            case "AUTHENTICATED":
                // base access
                break;
        }
    }

}
