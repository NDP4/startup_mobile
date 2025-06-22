package com.appku.bookingbus.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BookingListResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.databinding.FragmentBookingHistoryBinding;
import com.appku.bookingbus.utils.SessionManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryFragment extends Fragment {
    private FragmentBookingHistoryBinding binding;
    private BookingHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookingHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new BookingHistoryAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(booking -> {
            Intent intent = new Intent(getContext(), BookingDetailActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadBookings);

        loadBookings();
    }

    private void loadBookings() {
        binding.swipeRefresh.setRefreshing(true);
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getBookings(token)
            .enqueue(new Callback<BookingListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingListResponse> call, @NonNull Response<BookingListResponse> response) {
                    binding.swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Booking> bookings = response.body().getData();
                        adapter.setBookings(bookings);
                        binding.emptyView.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
                        if (bookings.isEmpty()) {
                            binding.emptyView.setText("Anda belum memiliki riwayat sewa");
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load bookings", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingListResponse> call, @NonNull Throwable t) {
                    binding.swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
