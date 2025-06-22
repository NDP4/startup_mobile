package com.appku.bookingbus.ui.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appku.bookingbus.R;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BookingDetailResponse;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.databinding.ActivityBookingDetailBinding;
import com.appku.bookingbus.utils.SessionManager;
import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {
    private ActivityBookingDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int bookingId = getIntent().getIntExtra("booking_id", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Invalid booking", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.btnBack.setOnClickListener(v -> finish());

        loadBookingDetail(bookingId);
    }

    private void loadBookingDetail(int bookingId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + new SessionManager(this).getToken();
        ApiClient.getInstance().getService().getBookingDetail(token, bookingId)
                .enqueue(new Callback<BookingDetailResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<BookingDetailResponse> call, @NonNull Response<BookingDetailResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            showBookingDetail(response.body().getData());
                        } else {
                            Toast.makeText(BookingDetailActivity.this, "Failed to load detail", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookingDetailResponse> call, @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(BookingDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void showBookingDetail(Booking booking) {
        // Ambil gambar bus dari ListBus berdasarkan ID bus
        loadBusImage(booking.getBus().getId());

        // Set bus info
        binding.tvBusName.setText(booking.getBus().getName());
        binding.tvNumberPlate.setText(booking.getBus().getNumberPlate());

        // Set status chip
        binding.chipStatus.setText(booking.getStatus());
        // Optionally set chip color based on status

        // Set tanggal sewa & kembali
        binding.tvBookingDate.setText(booking.getBookingDate());
        binding.tvReturnDate.setText(booking.getReturnDate());

        // Booking Info
        binding.tvPickupLocation.setText(booking.getPickupLocation());
        binding.tvDestination.setText(booking.getDestination());
        binding.tvTotalSeats.setText(String.valueOf(booking.getTotalSeats()));
        binding.tvSeatType.setText(booking.getSeatType());
        binding.tvSpecialRequests.setText(
                booking.getSpecialRequests() == null || booking.getSpecialRequests().isEmpty() ? "-" : booking.getSpecialRequests()
        );

        // Payment Info
        binding.tvTotalAmount.setText(String.format("Total: Rp %,d", (long) booking.getTotalAmount()));
        binding.tvPaymentStatus.setText("Payment Status: " + booking.getPaymentStatus());
    }

    private void loadBusImage(int busId) {
        // Default placeholder
        binding.ivBusImage.setImageResource(R.drawable.bus_placeholder);

        String token = "Bearer " + new SessionManager(this).getToken();
        ApiClient.getInstance().getService().getBuses(token)
            .enqueue(new retrofit2.Callback<BusListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BusListResponse> call, @NonNull Response<BusListResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        for (ListBus bus : response.body().getData().getData()) {
                            if (bus.getId() == busId) {
                                String imageUrl = "https://bus.ndp.my.id/storage/" + bus.getMainImage();
                                Glide.with(BookingDetailActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.bus_placeholder)
                                        .error(R.drawable.bus_placeholder)
                                        .into(binding.ivBusImage);
                                break;
                            }
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<BusListResponse> call, @NonNull Throwable t) {
                    // Do nothing, keep placeholder
                }
            });
    }
}
