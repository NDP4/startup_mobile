package com.appku.bookingbus.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appku.bookingbus.adapter.BusAdapter;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.BookingListResponse;
import com.appku.bookingbus.api.response.BusListResponse;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.data.model.ListBus;
import com.appku.bookingbus.databinding.FragmentDashboardBinding;
import com.appku.bookingbus.ui.booking.BookingDetailActivity;
import com.appku.bookingbus.ui.booking.BookingHistoryAdapter;
import com.appku.bookingbus.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private BookingHistoryAdapter bookingAdapter;
    private BusAdapter latestBusAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        // Header
        String userName = new SessionManager(requireContext()).getUserName();
        binding.tvDashboardWelcome.setText(userName != null ? "Hi, " + userName + "!" : "Hi, Guest!");
        binding.tvDashboardSubtitle.setText("Selamat datang di BookingBus");

        // Riwayat Booking
        bookingAdapter = new BookingHistoryAdapter();
        binding.rvBookingHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBookingHistory.setAdapter(bookingAdapter);

        bookingAdapter.setOnItemClickListener(booking -> {
            Intent intent = new Intent(getContext(), BookingDetailActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        });

        // Bus Terbaru
        latestBusAdapter = new BusAdapter(requireContext());
        binding.rvLatestBus.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatestBus.setAdapter(latestBusAdapter);

        loadBookingHistory();
        loadLatestBus();

        return binding.getRoot();
    }

    private void loadBookingHistory() {
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getBookings(token)
            .enqueue(new Callback<BookingListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingListResponse> call, @NonNull Response<BookingListResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Booking> bookings = response.body().getData();
                        bookingAdapter.setBookings(bookings);
                        binding.tvEmptyBooking.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        binding.tvEmptyBooking.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Gagal memuat riwayat sewa", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingListResponse> call, @NonNull Throwable t) {
                    binding.tvEmptyBooking.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadLatestBus() {
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getBuses(token)
            .enqueue(new Callback<BusListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BusListResponse> call, @NonNull Response<BusListResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<ListBus> buses = response.body().getData().getData();
                        if (buses != null && !buses.isEmpty()) {
                            // Sort by id descending (latest first)
                            List<ListBus> sorted = new ArrayList<>(buses);
                            Collections.sort(sorted, (a, b) -> Integer.compare(b.getId(), a.getId()));
                            // Show only 5 latest buses
                            List<ListBus> latest = sorted.subList(0, Math.min(5, sorted.size()));
                            latestBusAdapter.setBuses(latest);
                            binding.tvEmptyLatestBus.setVisibility(View.GONE);
                        } else {
                            latestBusAdapter.setBuses(new ArrayList<>());
                            binding.tvEmptyLatestBus.setVisibility(View.VISIBLE);
                        }
                    } else {
                        latestBusAdapter.setBuses(new ArrayList<>());
                        binding.tvEmptyLatestBus.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Gagal memuat bus terbaru", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BusListResponse> call, @NonNull Throwable t) {
                    latestBusAdapter.setBuses(new ArrayList<>());
                    binding.tvEmptyLatestBus.setVisibility(View.VISIBLE);
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