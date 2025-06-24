package com.appku.bookingbus.ui.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.appku.bookingbus.R;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.request.BookingRequest;
import com.appku.bookingbus.api.request.PaymentRequest;
import com.appku.bookingbus.api.response.BookingResponse;
import com.appku.bookingbus.api.response.PaymentResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.databinding.ActivityBookingBinding;
import com.appku.bookingbus.ui.payment.PaymentWebViewActivity;
import com.appku.bookingbus.utils.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private ActivityBookingBinding binding;
    private SessionManager sessionManager;
    private final Calendar bookingDate = Calendar.getInstance();
    private final Calendar returnDate = Calendar.getInstance();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private int busId;
    private int maxSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        busId = getIntent().getIntExtra("bus_id", -1);
        maxSeats = getIntent().getIntExtra("max_seats", 0);

        if (busId == -1 || maxSeats == 0) {
            Toast.makeText(this, "Invalid bus data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViews();
        setupListeners();
    }

    private void setupViews() {
        binding.tilTotalSeats.setHint("Total Seats (Max: " + maxSeats + ")");
        returnDate.add(Calendar.DATE, 1); // Set return date to tomorrow by default
        updateDateDisplay();
        
        // Set default seat type to standard
        binding.chipStandard.setChecked(true);
        binding.chipStandard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.chipStandard.setChipBackgroundColorResource(R.color.primary);
                binding.chipStandard.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                binding.chipStandard.setChipBackgroundColorResource(android.R.color.transparent);
                binding.chipStandard.setTextColor(getResources().getColor(android.R.color.black));
            }
        });
        
        binding.chipLegrest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.chipLegrest.setChipBackgroundColorResource(R.color.primary);
                binding.chipLegrest.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                binding.chipLegrest.setChipBackgroundColorResource(android.R.color.transparent);
                binding.chipLegrest.setTextColor(getResources().getColor(android.R.color.black));
            }
        });
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        binding.tilBookingDate.setEndIconOnClickListener(v -> showDatePicker(true));
        binding.tilReturnDate.setEndIconOnClickListener(v -> showDatePicker(false));

        binding.btnBook.setOnClickListener(v -> validateAndBook());
    }

    private void showDatePicker(boolean isBookingDate) {
        Calendar calendar = isBookingDate ? bookingDate : returnDate;
        Calendar minDate = isBookingDate ? Calendar.getInstance() : bookingDate;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                updateDateDisplay();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        binding.etBookingDate.setText(dateFormatter.format(bookingDate.getTime()));
        binding.etReturnDate.setText(dateFormatter.format(returnDate.getTime()));
    }

    private void validateAndBook() {
        String pickupLocation = binding.etPickupLocation.getText().toString().trim();
        String destination = binding.etDestination.getText().toString().trim();
        String totalSeatsStr = binding.etTotalSeats.getText().toString().trim();
        String specialRequests = binding.etSpecialRequests.getText().toString().trim();
        String seatType = binding.chipLegrest.isChecked() ? "legrest" : "standard";

        if (pickupLocation.isEmpty()) {
            binding.tilPickupLocation.setError("Please enter pickup location");
            return;
        }

        if (destination.isEmpty()) {
            binding.tilDestination.setError("Please enter destination");
            return;
        }

        if (totalSeatsStr.isEmpty()) {
            binding.tilTotalSeats.setError("Please enter total seats");
            return;
        }

        int totalSeats = Integer.parseInt(totalSeatsStr);
        if (totalSeats <= 0 || totalSeats > maxSeats) {
            binding.tilTotalSeats.setError("Invalid number of seats");
            return;
        }

        // Clear any previous errors
        binding.tilPickupLocation.setError(null);
        binding.tilDestination.setError(null);
        binding.tilTotalSeats.setError(null);

        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnBook.setEnabled(false);

        // Prepare booking data
//        Map<String, String> bookingData = new HashMap<>();
//        bookingData.put("booking_date", dateFormatter.format(bookingDate.getTime()));
//        bookingData.put("return_date", dateFormatter.format(returnDate.getTime()));
//        bookingData.put("pickup_location", pickupLocation);
//        bookingData.put("destination", destination);
//        bookingData.put("total_seats", String.valueOf(totalSeats));
//        bookingData.put("seat_type", seatType);
//        bookingData.put("special_requests", specialRequests);
        BookingRequest bookingRequest = new BookingRequest(
                dateFormatter.format(bookingDate.getTime()),
                dateFormatter.format(returnDate.getTime()),
                pickupLocation,
                destination,
                totalSeats,
                seatType,
                specialRequests
        );

        // log sebelum memanggil API
        Log.d("BookingActivity", "Mengirim request booking...");
        Log.d("BookingActivity", "URL: " + ApiClient.getInstance().getService().createBooking("Bearer " + sessionManager.getToken(), busId, bookingRequest).request().url().toString());
        Log.d("BookingActivity", "Request: " + new Gson().toJson(bookingRequest));

        // Make API call
        String token = "Bearer " + sessionManager.getToken();
        ApiClient.getInstance().getService().createBooking(token, busId, bookingRequest)
            .enqueue(new Callback<BookingResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingResponse> call, @NonNull Response<BookingResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Ambil booking dari response
                        BookingResponse.Data data = response.body().getData();
                        Booking booking = (data != null) ? data.getBooking() : null;
                        int bookingId = (booking != null) ? booking.getId() : -1;
                        double amount = (booking != null) ? booking.getTotalAmount() : 0;

                        if (bookingId > 0) {
                            // Setelah booking sukses, buka detail booking
                            Intent intent = new Intent(BookingActivity.this, BookingDetailActivity.class);
                            intent.putExtra("booking_id", bookingId);
                            startActivity(intent);
                            finish();
                        } else {
                            // Tambahkan log seluruh response JSON untuk debug
                            try {
                                String rawJson = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : new Gson().toJson(response.body());
                                Log.e("BookingActivity", "Booking ID not found. Raw response: " + rawJson);
                                Toast.makeText(BookingActivity.this, "Booking gagal: Data booking tidak ditemukan. Silakan coba lagi atau hubungi admin.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e("BookingActivity", "Error reading raw response", e);
                            }
                            binding.progressBar.setVisibility(View.GONE);
                            binding.btnBook.setEnabled(true);
                        }
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnBook.setEnabled(true);
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            } else if (response.body() != null) {
                                errorBody = new Gson().toJson(response.body());
                            }
                        } catch (Exception e) {
                            errorBody = "Error reading errorBody: " + e.getMessage();
                        }
                        Log.e("BookingActivity", "Booking failed: " + response.code() + " | " + errorBody);
                        Toast.makeText(BookingActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingResponse> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnBook.setEnabled(true);

                    Log.e("BookingActivity", "Booking error: ", t);

                    String errorMessage = "Network error";
                    if (t instanceof IOException) {
                        errorMessage = "Please check your internet connection";
                    } else if (t instanceof SocketTimeoutException) {
                        errorMessage = "Request timed out. Please try again";
                    }

                    Toast.makeText(BookingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
    }

    // Perbarui createPayment agar menerima semua field
    private void createPayment(int bookingId, double amount, String paymentType, Map<String, Object> paymentDetails) {
        PaymentRequest paymentRequest = new PaymentRequest(bookingId, amount, paymentType, paymentDetails);

        String token = "Bearer " + sessionManager.getToken();

        ApiClient.getInstance().getService().createPayment(token, paymentRequest)
            .enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(@NonNull Call<PaymentResponse> call, @NonNull Response<PaymentResponse> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnBook.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Buka WebView dengan URL pembayaran
                        Intent intent = new Intent(BookingActivity.this, PaymentWebViewActivity.class);
                        intent.putExtra("payment_url", response.body().getData().getPaymentUrl());
                        startActivity(intent);
                        finish();
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Error reading errorBody: " + e.getMessage();
                        }
                        Log.e("BookingActivity", "Payment failed: " + response.code() + " | " + errorBody);
                        Toast.makeText(BookingActivity.this, "Payment initialization failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PaymentResponse> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnBook.setEnabled(true);
                    Log.e("BookingActivity", "Payment error: ", t);
                    Toast.makeText(BookingActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }
}