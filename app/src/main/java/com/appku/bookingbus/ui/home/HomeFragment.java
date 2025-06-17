package com.appku.bookingbus.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appku.bookingbus.adapter.BusAdapter;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.databinding.FragmentHomeBinding;
import com.appku.bookingbus.data.model.Bus;
import com.appku.bookingbus.utils.SessionManager;

import java.util.List;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        
        setupBusList();
        observeData();
        
        return root;
    }
    
    private void setupBusList() {
        busAdapter = new BusAdapter(requireContext());
        binding.rvBusList.setAdapter(busAdapter);
        binding.rvBusList.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        busAdapter.setOnItemClickListener(bus -> {
            // Handle bus item click
            Toast.makeText(requireContext(), "Selected: " + bus.getName(), Toast.LENGTH_SHORT).show();
        });
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}