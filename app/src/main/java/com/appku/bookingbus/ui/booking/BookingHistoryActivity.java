package com.appku.bookingbus.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BookingListResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.databinding.ActivityBookingHistoryBinding;
import com.appku.bookingbus.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {
    private ActivityBookingHistoryBinding binding;
    private BookingHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new BookingHistoryAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(booking -> {
            Intent intent = new Intent(this, BookingDetailActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadBookings);

        loadBookings();
    }

    private void loadBookings() {
        binding.swipeRefresh.setRefreshing(true);
        String token = "Bearer " + new SessionManager(this).getToken();
        ApiClient.getInstance().getService().getBookings(token)
            .enqueue(new Callback<BookingListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingListResponse> call, @NonNull Response<BookingListResponse> response) {
                    binding.swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Booking> bookings = response.body().getData();
                        adapter.setBookings(bookings);
                        binding.emptyView.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        Toast.makeText(BookingHistoryActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingListResponse> call, @NonNull Throwable t) {
                    binding.swipeRefresh.setRefreshing(false);
                    Toast.makeText(BookingHistoryActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
