package com.appku.bookingbus.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appku.bookingbus.adapter.BusAdapter;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.databinding.FragmentHomeBinding;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.utils.SessionManager;

import java.util.List;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private BusAdapter busAdapter;
    private HomeViewModel homeViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1002;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        setupBusList();
        setupLocation();
        observeData();
        
        return root;
    }
    
    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateLocationUI(location);
                }
            }
        };

        if (!isLocationEnabled()) {
            promptEnableLocation();
        } else {
            checkAndRequestLocationPermissions();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && 
               (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void promptEnableLocation() {
        Toast.makeText(requireContext(), "Please enable GPS", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void checkAndRequestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            requestBackgroundLocationPermission();
            startLocationUpdates();
        }
    }

    private void requestBackgroundLocationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            }, BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)  // 10 seconds
                .setFastestInterval(5000);  // 5 seconds

        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

        // Get last known location immediately
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                updateLocationUI(location);
            }
        });
    }

    private void updateLocationUI(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String stateName = address.getAdminArea();
                String locationText = String.format("%s, %s", cityName, stateName);
                binding.tvLocation.setText(locationText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupBusList() {
        busAdapter = new BusAdapter(requireContext());
        binding.rvBusList.setAdapter(busAdapter);
        binding.rvBusList.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        busAdapter.setOnItemClickListener(bus -> {
            // Handle bus item click
            Toast.makeText(requireContext(), "Selected: " + bus.getName(), Toast.LENGTH_SHORT).show();
        });
        
        loadBuses(); // Load buses data
    }
    
    private void observeData() {
        // Observe user data for welcome message
        homeViewModel.getText().observe(getViewLifecycleOwner(), text -> {
            binding.tvWelcome.setText(text);
        });
        
        // Observe bus list
        homeViewModel.getBuses().observe(getViewLifecycleOwner(), buses -> {
            if (buses != null && !buses.isEmpty()) {
                android.util.Log.d("HomeFragment", "Received buses: " + buses.size());
                busAdapter.setBuses(buses);
                busAdapter.notifyDataSetChanged();
            } else {
                android.util.Log.e("HomeFragment", "No buses received");
            }
        });
    }

    private void loadBuses() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        SessionManager sessionManager = new SessionManager(requireContext());
        String token = "Bearer " + sessionManager.getToken();
        
        ApiClient.getInstance().getService().getBuses(token).enqueue(new Callback<BusListResponse>() {
            @Override
            public void onResponse(@NonNull Call<BusListResponse> call, @NonNull Response<BusListResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                
                if (response.isSuccessful() && response.body() != null) {
                    BusListResponse busResponse = response.body();
                    if (busResponse.isSuccess() && busResponse.getData() != null) {
                        busAdapter.setBuses(busResponse.getData().getData());
                    }
                } else {
                    showError("Failed to load buses");
                }
            }

            @Override
            public void onFailure(@NonNull Call<BusListResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestBackgroundLocationPermission();
                startLocationUpdates();
            } else {
                Toast.makeText(requireContext(), 
                    "Location permission is required for this feature", 
                    Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        binding = null;
    }
}