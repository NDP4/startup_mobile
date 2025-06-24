package com.appku.bookingbus.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appku.bookingbus.R;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.request.PaymentRequest;
import com.appku.bookingbus.api.response.BookingDetailResponse;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.api.response.PaymentResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.databinding.ActivityBookingDetailBinding;
import com.appku.bookingbus.ui.payment.PaymentWebViewActivity;
import com.appku.bookingbus.utils.SessionManager;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {
    private ActivityBookingDetailBinding binding;
    private Booking currentBooking;

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

        // Tambahkan tombol bayar (hidden dulu)
        binding.btnPay.setVisibility(View.GONE);
        binding.btnPay.setOnClickListener(v -> {
            if (currentBooking != null) {
                startPayment(currentBooking);
            }
        });

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
        currentBooking = booking;
        // Ambil gambar bus dari ListBus berdasarkan ID bus
        loadBusImage(booking.getBus().getId());

        // Set bus info
        binding.tvBusName.setText(booking.getBus().getName());
        binding.tvNumberPlate.setText(booking.getBus().getNumberPlate());

        // Set status chip
        binding.chipStatus.setText(booking.getStatus());
        // Optionally set chip color based on status

        // Set tanggal sewa & kembali
        binding.tvBookingDate.setText(formatDate(booking.getBookingDate()));
        binding.tvReturnDate.setText(formatDate(booking.getReturnDate()));

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

        // Tampilkan tombol bayar jika status pending
        if ("pending".equalsIgnoreCase(booking.getPaymentStatus())) {
            binding.btnPay.setVisibility(View.VISIBLE);
        } else {
            binding.btnPay.setVisibility(View.GONE);
        }
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

    private void startPayment(Booking booking) {
        binding.progressBar.setVisibility(View.VISIBLE);
        String token = "Bearer " + new SessionManager(this).getToken();

        // Kirim payment_details sebagai Map kosong, jangan null!
        Map<String, Object> paymentDetails = new HashMap<>(); // <-- pastikan selalu objek kosong

        PaymentRequest paymentRequest = new PaymentRequest(
                booking.getId(),
                booking.getTotalAmount(),
                "midtrans", // GUNAKAN "midtrans" agar backend trigger Snap
                paymentDetails // <-- pastikan ini tidak null
        );

        ApiClient.getInstance().getService().createPayment(token, paymentRequest)
                .enqueue(new retrofit2.Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentResponse> call, @NonNull Response<PaymentResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String snapToken = booking.getSnapToken();
                            String paymentUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + snapToken; // atau production jika sudah live

                            Intent intent = new Intent(BookingDetailActivity.this, PaymentWebViewActivity.class);
                            intent.putExtra("payment_url", paymentUrl);
                            startActivity(intent);
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                } else if (response.body() != null) {
                                    errorBody = response.body().getMessage();
                                }
                            } catch (Exception e) {
                                errorBody = "Error reading errorBody: " + e.getMessage();
                            }
                            android.util.Log.e("BookingDetailActivity", "Payment failed: " + response.code() + " | " + errorBody);
                            Toast.makeText(BookingDetailActivity.this, "Gagal inisialisasi pembayaran: " + errorBody, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentResponse> call, @NonNull Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(BookingDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatDate(String dateTime) {
        if (dateTime == null) return "-";
        try {
            return dateTime.split("T")[0];
        } catch (Exception e) {
            return dateTime;
        }
    }
}
