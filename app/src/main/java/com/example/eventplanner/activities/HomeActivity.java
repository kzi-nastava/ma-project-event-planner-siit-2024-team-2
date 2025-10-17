package com.example.eventplanner.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spanned;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.eventplanner.EventPlannerApp;
import com.example.eventplanner.R;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.databinding.ActivityHomeBinding;
import com.example.eventplanner.utils.FormatUtil;
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
    private final Set<Integer> topLevelDestinations = new HashSet<>();

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

        // Top-level destinations
        topLevelDestinations.add(R.id.nav_home);
        topLevelDestinations.add(R.id.nav_events);
        topLevelDestinations.add(R.id.nav_service_products);
        topLevelDestinations.add(R.id.nav_website);
        topLevelDestinations.add(R.id.nav_logout);

        // Protected routes — user must be logged in
        protectedDestinations.add(R.id.nav_notifications);
        protectedDestinations.add(R.id.nav_fragment_create_product);
        protectedDestinations.add(R.id.nav_fragment_my_products);
        protectedDestinations.add(R.id.nav_fragment_create_event);
        protectedDestinations.add(R.id.nav_fragment_profile);
        protectedDestinations.add(R.id.nav_services);
        protectedDestinations.add(R.id.nav_edit_service);
        protectedDestinations.add(R.id.nav_create_event_type);
        protectedDestinations.add(R.id.nav_event_types);

        Set<Integer> combinedDestinations = new HashSet<>();
        combinedDestinations.addAll(topLevelDestinations);
        combinedDestinations.addAll(protectedDestinations);

        appBarConfiguration = new AppBarConfiguration.Builder(combinedDestinations)
                .setOpenableLayout(drawerLayout)
                .build();

        // Setup navigation UI
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Add route protection
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

        if (getIntent().getBooleanExtra("com.example.event_planner.navigateToNotifications", false))
            navController.navigate(R.id.nav_notifications);

        subscribeToNotifications();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("com.example.event_planner.navigateToNotifications", false))
            navController.navigate(R.id.nav_notifications);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        if (UserRoleUtils.getUserRole(this) == null)
            getMenuInflater().inflate(R.menu.toolbar_menu_guest, menu);
        else
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

        String role = UserRoleUtils.getUserRole(this);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(false);
        }

        // Common for all users
        menu.findItem(R.id.nav_home).setVisible(true);
        menu.findItem(R.id.nav_events).setVisible(true);
        menu.findItem(R.id.nav_service_products).setVisible(true);

        if (role == null) {
            menu.findItem(R.id.nav_logout).setTitle(R.string.login);
            return;
        }

        // Authenticated users
        menu.findItem(R.id.nav_notifications).setVisible(true);

        switch (role) {
            case "ADMIN":
                menu.findItem(R.id.nav_event_types).setVisible(true);
                menu.findItem(R.id.nav_create_event_type).setVisible(true);
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


    private void subscribeToNotifications() {
        long userId = UserIdUtils.getUserId(this);
        if (userId != -1) {
            EventPlannerApp.getWebSocketManager().getChannel("notifications", "", Long.toString(userId))
                    .observe(this, wsMessage -> {
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.putExtra("com.example.event_planner.navigateToNotifications", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


                        Spanned title = FormatUtil.markdownToSpanned(wsMessage.getTitle());
                        Spanned body = FormatUtil.markdownToSpanned(wsMessage.getMessage());
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifications")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.POST_NOTIFICATIONS }, 1);
                            return;
                        }
                        NotificationManagerCompat.from(this).notify((int)(SystemClock.uptimeMillis() % Integer.MAX_VALUE), builder.build());
                    });
        }
    }

}
