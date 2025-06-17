package com.appku.bookingbus.ui.detail;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import com.appku.bookingbus.ui.booking.BookingActivity;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.appku.bookingbus.R;
import com.appku.bookingbus.adapter.ImageSliderAdapter;
import com.appku.bookingbus.adapter.TabImageAdapter;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.api.response.BusResponse;
import com.appku.bookingbus.data.model.DetailBus;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.databinding.ActivityDetailBusBinding;
import com.appku.bookingbus.utils.SessionManager;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailBusActivity extends AppCompatActivity {
    private ActivityDetailBusBinding binding;
    private static final String BASE_URL = "https://bus.ndp.my.id/storage/";
    private SessionManager sessionManager;
    private DetailBus bus;

    private List<String> imageUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        sessionManager = new SessionManager(this);
        
        // Set window animation
        overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        
        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.contentLayout.setVisibility(View.GONE);
        
        int busId = getIntent().getIntExtra("bus_id", -1);
        if (busId != -1) {
            loadBusDetails(busId);
        } else {
            Toast.makeText(this, "Error: Bus ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        binding.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("bus_id", busId);
            intent.putExtra("max_seats", bus.getDefault_seat_capacity());
            startActivity(intent);
        });
    }
    
    private void setupBusDetails(DetailBus bus) {
        this.bus = bus;
        binding.tvBusName.setText(bus.getName());
        binding.tvPlate.setText(bus.getNumber_plate());
        binding.tvDescription.setText(bus.getDescription());
        binding.tvCapacity.setText(String.format("%d Seats", bus.getDefault_seat_capacity()));
        
        // Format price
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedPrice;
        if (bus.getPricing_type().equals("daily")) {
            formattedPrice = formatter.format(Double.parseDouble(bus.getPrice_per_day())).replace(",00", "");
            binding.tvPrice.setText(String.format("%s/day", formattedPrice));
        } else {
            formattedPrice = formatter.format(Double.parseDouble(bus.getPrice_per_km())).replace(",00", "");
            binding.tvPrice.setText(String.format("%s/km", formattedPrice));
        }
        
        // Get legrest price from ListResponse instead
        ApiClient.getInstance().getService().getBuses("Bearer " + sessionManager.getToken()).enqueue(new Callback<BusListResponse>() {
            @Override
            public void onResponse(@NonNull Call<BusListResponse> call, @NonNull Response<BusListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ListBus> buses = response.body().getData().getData();
                    for (ListBus listBus : buses) {
                        if (listBus.getId() == bus.getId()) {
                            String legrestPrice = formatter.format(Double.parseDouble(listBus.getLegrest_price_per_seat())).replace(",00", "");
                            binding.tvLegrestPrice.setText(String.format("Legrest: %s/seat", legrestPrice));
                            binding.tvLegrestPrice.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    binding.tvLegrestPrice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BusListResponse> call, @NonNull Throwable t) {
                binding.tvLegrestPrice.setVisibility(View.GONE);
            }
        });
        
        // Set status
        String status = bus.getStatus().substring(0, 1).toUpperCase() + bus.getStatus().substring(1).toLowerCase();
        binding.chipStatus.setText(status);
        
        int chipColor;
        switch (bus.getStatus().toLowerCase()) {
            case "available":
                chipColor = R.color.primary;
                break;
            case "maintenance":
                chipColor = android.R.color.holo_orange_dark;
                break;
            case "booked":
                chipColor = android.R.color.holo_red_light;
                break;
            default:
                chipColor = android.R.color.darker_gray;
                break;
        }
        binding.chipStatus.setChipBackgroundColorResource(chipColor);
    }
    
    private void setupImageSlider(DetailBus bus) {
        // Prepare image urls
        List<String> imageUrls = new ArrayList<>();
        if (bus.getImages() != null) {
            for (DetailBus.Image image : bus.getImages()) {
                String imageUrl = BASE_URL + image.getUrl();
                imageUrls.add(imageUrl);
            }
        }
        
        // Setup main slider
        ImageSliderAdapter adapter = new ImageSliderAdapter();
        adapter.setImageUrls(imageUrls);
        binding.viewPager.setAdapter(adapter);
        
        // Create tabs first
        for (int i = 0; i < imageUrls.size(); i++) {
            binding.tabLayoutIndicator.addTab(binding.tabLayoutIndicator.newTab());
        }
        
        // Setup tab indicators
        new TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPager, (tab, position) -> {
            // Leave tab empty, will be populated by TabImageAdapter
        }).attach();
        
        // Setup custom tab views after tabs are created
        new TabImageAdapter(this, binding.tabLayoutIndicator, imageUrls);
        
        // Add tab selection listener
        binding.tabLayoutIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    tab.getCustomView().setAlpha(1f);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    tab.getCustomView().setAlpha(0.6f);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void loadBusDetails(int busId) {
        String token = "Bearer " + sessionManager.getToken();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.contentLayout.setVisibility(View.GONE);
        
        ApiClient.getInstance().getService().getBusById(token, busId).enqueue(new Callback<BusResponse>() {
            @Override
            public void onResponse(@NonNull Call<BusResponse> call, @NonNull Response<BusResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DetailBus bus = response.body().getData();
                    if (bus != null) {
                        binding.contentLayout.setVisibility(View.VISIBLE);
                        setupBusDetails(bus);
                        setupImageSlider(bus);
                    } else {
                        Toast.makeText(DetailBusActivity.this, "Bus data is empty", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    String errorMessage = "Failed to load bus details";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(DetailBusActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BusResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(DetailBusActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
    }
}