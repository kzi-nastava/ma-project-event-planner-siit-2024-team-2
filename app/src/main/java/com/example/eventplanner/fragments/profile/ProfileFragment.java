package com.example.eventplanner.fragments.profile;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventplanner.R;
import com.example.eventplanner.adapters.FavoriteEventsAdapter;
import com.example.eventplanner.adapters.FavoriteServiceProductsAdapter;
import com.example.eventplanner.clients.utils.AuthUtils;
import com.example.eventplanner.clients.utils.ImageUtil;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.clients.utils.UserRoleUtils;
import com.example.eventplanner.dto.event.EventSummaryDto;
import com.example.eventplanner.dto.serviceproduct.ServiceProductSummaryDto;
import com.example.eventplanner.dto.user.CompanyInfoDto;
import com.example.eventplanner.dto.user.UserDto;
import com.example.eventplanner.dto.user.UserInfoDto;
import com.example.eventplanner.fragments.event.EventDetailsFragment;
import com.example.eventplanner.fragments.services.ServiceProductDetailsFragment;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private Button btnUploadPicture, btnRemovePicture;
    private TextView tvName, tvEmail;
    private EditText etFirstName, etLastName, etPhone, etAddress;
    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnUpdatePersonalInfo, btnChangePassword, btnDeactivateAccount;
    private LinearLayout layoutChangePassword;

    // Company fields
    private EditText etCompanyName, etCompanyDescription;
    private Button btnUpdateCompany;

    // Favorites lists
    private RecyclerView rvFavoriteEvents, rvFavoriteServiceProducts;
    private FavoriteEventsAdapter eventsAdapter;
    private FavoriteServiceProductsAdapter productsAdapter;

    private MaterialSwitch switchReceiveNotifications;

    private Uri selectedImageUri;
    private ProfileViewModel profileViewModel;
    private long userId = -1;
    private String userRole = null;
    private View layoutCompanyInfo;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivProfilePicture.setImageURI(uri);
                    btnUploadPicture.setVisibility(View.VISIBLE);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        layoutCompanyInfo = view.findViewById(R.id.layoutCompanyInfo);

        // get userId early
        userId = UserIdUtils.getUserId(requireContext());
        userRole = UserRoleUtils.getUserRole(requireContext());
        if ("SERVICE_PRODUCT_PROVIDER".equals(userRole)) {
            layoutCompanyInfo.setVisibility(View.VISIBLE);
        } else {
            layoutCompanyInfo.setVisibility(View.GONE);
        }

        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        btnUploadPicture = view.findViewById(R.id.btnUploadPicture);
        btnRemovePicture = view.findViewById(R.id.btnRemovePicture);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);

        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnUpdatePersonalInfo = view.findViewById(R.id.btnUpdatePersonalInfo);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnDeactivateAccount = view.findViewById(R.id.btnDeactivateAccount);

        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);

        // company fields
        etCompanyName = view.findViewById(R.id.etCompanyName);
        etCompanyDescription = view.findViewById(R.id.etCompanyDescription);
        btnUpdateCompany = view.findViewById(R.id.btnUpdateCompany);

        // recycler views
        rvFavoriteEvents = view.findViewById(R.id.rvFavoriteEvents);
        rvFavoriteServiceProducts = view.findViewById(R.id.rvFavoriteServiceProducts);

        switchReceiveNotifications = view.findViewById(R.id.switchReceiveNotifications);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        eventsAdapter = new FavoriteEventsAdapter();
        eventsAdapter.setOnEventClickListener(new FavoriteEventsAdapter.OnEventClickListener() {
            @Override
            public void onMoreInfoClick(EventSummaryDto event) {
                // Navigate to EventDetailsFragment
                EventDetailsFragment detailsFragment = new EventDetailsFragment();
                Bundle args = new Bundle();
                args.putLong("eventId", event.getId()); // pass event id
                detailsFragment.setArguments(args);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.action_profile_to_event_details, args);
            }

            @Override
            public void onHeartClick(EventSummaryDto event) {
                if (userId != -1) {
                    profileViewModel.removeFavoriteEvent(userId, event.getId());
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    profileViewModel.loadFavoriteEvents(userId);
                }
            }
        });
        rvFavoriteEvents.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvFavoriteEvents.setAdapter(eventsAdapter);

        productsAdapter = new FavoriteServiceProductsAdapter();
        productsAdapter.setOnServiceProductClickListener(new FavoriteServiceProductsAdapter.OnServiceProductClickListener() {
            @Override
            public void onMoreInfoClick(ServiceProductSummaryDto serviceProduct) {
                ServiceProductDetailsFragment detailsFragment = new ServiceProductDetailsFragment();
                Bundle args = new Bundle();
                args.putLong("serviceProductId", serviceProduct.getId());
                detailsFragment.setArguments(args);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.fragment_nav_content_main);
                navController.navigate(R.id.action_profile_to_service_product_details, args);
            }

            @Override
            public void onHeartClick(ServiceProductSummaryDto product) {
                if (userId != -1) {
                    profileViewModel.removeFavoriteServiceProduct(userId, product.getId());
                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                    profileViewModel.loadFavoriteServiceProducts(userId);
                }
            }
        });
        rvFavoriteServiceProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rvFavoriteServiceProducts.setAdapter(productsAdapter);

        profileViewModel.getUser().observe(getViewLifecycleOwner(), this::populateUserData);

        profileViewModel.getFavoriteEvents().observe(getViewLifecycleOwner(), events -> {
            if (events != null) eventsAdapter.setEvents(events);
            else eventsAdapter.setEvents(new ArrayList<>());
        });

        profileViewModel.getFavoriteServiceProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) productsAdapter.setServiceProducts(products);
            else productsAdapter.setServiceProducts(new ArrayList<>());
        });

        profileViewModel.resetMessages();
        profileViewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Operation successful", Toast.LENGTH_SHORT).show();
                // reload user data so UI is fresh
                if (userId != -1) profileViewModel.loadUser(userId);
            }
        });

        profileViewModel.getUpdateMessageResource().observe(getViewLifecycleOwner(), resId -> {
            if (resId == null)
                return;
            Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
        });

        profileViewModel.getUpdateMessageText().observe(getViewLifecycleOwner(), text -> {
            if (text == null)
                return;
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        });

        ivProfilePicture.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnUploadPicture.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                profileViewModel.uploadProfilePicture(userId, getContext(), selectedImageUri);
            }
        });

        btnRemovePicture.setOnClickListener(v -> {
            profileViewModel.removeProfilePicture(userId);
        });

        btnUpdatePersonalInfo.setOnClickListener(v -> {
            UserInfoDto info = new UserInfoDto(
                    etFirstName.getText().toString(),
                    etLastName.getText().toString(),
                    etPhone.getText().toString(),
                    etAddress.getText().toString()
            );
            if (userId != -1) profileViewModel.updatePersonalInfo(userId, info);
            else Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString();
            String newPass = etNewPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userId != -1) profileViewModel.changePassword(userId, oldPass, newPass);
            else Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        });

        btnDeactivateAccount.setOnClickListener(v -> {
            if (userId != -1) profileViewModel.deleteAccount(userId);
            else Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        });

        btnUpdateCompany.setOnClickListener(v -> {
            CompanyInfoDto dto = new CompanyInfoDto(
                    etCompanyName.getText().toString(),
                    etCompanyDescription.getText().toString()
            );
            if (userId != -1) profileViewModel.updateCompanyInfo(userId, dto);
            else Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        });

        switchReceiveNotifications.setOnClickListener((switchView) -> {
            if (userId == -1)
                return;
            if (switchReceiveNotifications.isChecked())
                profileViewModel.unmuteNotifications(userId);
            else
                profileViewModel.muteNotifications(userId);
        });


        if (userId != -1) {
            profileViewModel.loadUser(userId);
            profileViewModel.loadFavoriteEvents(userId);
            profileViewModel.loadFavoriteServiceProducts(userId);
            profileViewModel.loadCompanyData(userId);
        } else {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void populateUserData(UserDto user) {
        if (user == null) return;

        tvName.setText((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName()));
        tvEmail.setText(user.getEmail() == null ? "" : user.getEmail());

        etFirstName.setText(user.getFirstName() == null ? "" : user.getFirstName());
        etLastName.setText(user.getLastName() == null ? "" : user.getLastName());
        etPhone.setText(user.getPhoneNumber() == null ? "" : user.getPhoneNumber());
        etAddress.setText(user.getAddress() == null ? "" : user.getAddress());

        // company
        if (etCompanyName != null)
            etCompanyName.setText(user.getCompanyName() == null ? "" : user.getCompanyName());
        if (etCompanyDescription != null)
            etCompanyDescription.setText(user.getCompanyDescription() == null ? "" : user.getCompanyDescription());

        // profile picture
        if (user.getImageEncodedName() != null && !user.getImageEncodedName().isEmpty()) {
            Glide.with(requireContext())
                    .load(ImageUtil.getImageUrl(user.getImageEncodedName()))
                    .placeholder(R.drawable.profile_picture)
                    .into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.profile_picture);
        }

        switchReceiveNotifications.setChecked(!user.isMutedNotifications());
        if (AuthUtils.isAuthenticatedUser(getContext()))
            layoutChangePassword.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        profileViewModel.resetMessages();
        super.onResume();
    }

}
