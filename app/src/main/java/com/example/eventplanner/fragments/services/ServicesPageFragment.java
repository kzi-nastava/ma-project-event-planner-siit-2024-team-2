package com.example.eventplanner.fragments.services;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventplanner.R;
import com.example.eventplanner.databinding.FragmentServicesPageBinding;
import com.example.eventplanner.model.serviceproduct.Service;
import com.example.eventplanner.model.serviceproduct.ServiceProductCategory;

import java.util.ArrayList;

public class ServicesPageFragment extends Fragment {

    public static ArrayList<Service> services = new ArrayList<Service>();
    private FragmentServicesPageBinding binding;

    public static ServicesPageFragment newInstance() {
        return new ServicesPageFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServicesPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        prepareProductList(services);

        FragmentTransition.to(ServicesListFragment.newInstance(services), getActivity(),
                false, R.id.scroll_services_list);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void prepareProductList(ArrayList<Service> services){
        services.clear();
//        services.add(new Service(1L, "Your catering", "We offer a brilliant service of catering. Don't worry because we're coming while your meal is still hot! Lorem ipsum  tra la la...", R.drawable.catering));
//        services.add(new Service(2L, "Our catering", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor", R.drawable.catering));
//        services.add(new Service(3L, "My catering", "We offer a brilliant service of catering. Don't worry because we're coming while your meal is still hot! Lorem ipsum  tra la la...", R.drawable.catering));
        Parcel parcel = Parcel.obtain();

        // Write data to the Parcel in the same order as the constructor reads it
        parcel.writeByte((byte) 1); // Indicates that `id` is not null
        parcel.writeLong(12345L); // Example ID

        ServiceProductCategory serviceProductCategory = new ServiceProductCategory(1L, "catering", "");
        parcel.writeParcelable(serviceProductCategory, 0); // Example for `category`

        parcel.writeByte((byte) 1); // `available` as true
        parcel.writeByte((byte) 0); // `visible` as false
        parcel.writeDouble(99.99); // Example price
        parcel.writeDouble(10.0); // Example discount
        parcel.writeString("Service Name"); // Example name
        parcel.writeString("Service Description"); // Example description
        parcel.writeStringList(new ArrayList<>()); // Example images list
        parcel.writeTypedList(new ArrayList<>()); // Example event types
        parcel.writeParcelable(null, 0); // Example for `serviceProductProvider`
        parcel.writeString("Some specifications"); // Example specifies
        parcel.writeFloat(1.5f); // Example duration
        parcel.writeFloat(0.5f); // Example minEngagementDuration
        parcel.writeFloat(2.0f); // Example maxEngagementDuration
        parcel.writeInt(30); // Example reservationDaysDeadline
        parcel.writeInt(7); // Example cancellationDaysDeadline
        parcel.writeByte((byte) 1); // `hasAutomaticReservation` as true

        // Reset the Parcel for reading
        parcel.setDataPosition(0);

        // Create a Service object using the Parcel
        Service service = new Service(parcel);
        services.add(service);

        // Release the Parcel object
        parcel.recycle();
    }
}