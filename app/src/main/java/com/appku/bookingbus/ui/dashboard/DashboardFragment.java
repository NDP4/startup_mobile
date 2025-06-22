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
import com.appku.bookingbus.utils.CacheManager;
import com.appku.bookingbus.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private ShimmerFrameLayout shimmerBooking, shimmerLatestBus;
    private CacheManager cacheManager;
    private static final String CACHE_KEY_BOOKING_LIST = "booking_list";
    private static final String CACHE_KEY_BOOKING_COUNT = "booking_count";

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

        shimmerBooking = binding.shimmerBookingHistory;
        shimmerLatestBus = binding.shimmerLatestBus;
        cacheManager = new CacheManager(requireContext());

        loadBookingHistory();
        loadLatestBus();

        return binding.getRoot();
    }

    private void loadBookingHistory() {
        shimmerBooking.setVisibility(View.VISIBLE);
        shimmerBooking.startShimmer();
        binding.rvBookingHistory.setVisibility(View.GONE);

        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getBookings(token)
            .enqueue(new Callback<BookingListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookingListResponse> call, @NonNull Response<BookingListResponse> response) {
                    if (binding == null || !isAdded()) return;
                    shimmerBooking.stopShimmer();
                    shimmerBooking.setVisibility(View.GONE);
                    binding.rvBookingHistory.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Booking> bookings = response.body().getData();
                        int serverCount = bookings.size();
                        int cachedCount = (int) cacheManager.getLong(CACHE_KEY_BOOKING_COUNT);
                        if (serverCount == cachedCount) {
                            // Tidak ada perubahan, load dari cache
                            String cachedJson = cacheManager.getString(CACHE_KEY_BOOKING_LIST);
                            if (cachedJson != null) {
                                List<Booking> cachedList = new Gson().fromJson(cachedJson, new TypeToken<List<Booking>>(){}.getType());
                                bookingAdapter.setBookings(cachedList);
                                binding.tvEmptyBooking.setVisibility(cachedList.isEmpty() ? View.VISIBLE : View.GONE);
                                if (cachedList.isEmpty()) {
                                    binding.tvEmptyBooking.setText("Anda belum memiliki riwayat sewa");
                                }
                                return;
                            }
                        }
                        // Ada perubahan, update cache dan UI
                        bookingAdapter.setBookings(bookings);
                        cacheManager.saveString(CACHE_KEY_BOOKING_LIST, new Gson().toJson(bookings));
                        cacheManager.saveLong(CACHE_KEY_BOOKING_COUNT, serverCount);
                        binding.tvEmptyBooking.setVisibility(bookings.isEmpty() ? View.VISIBLE : View.GONE);
                        if (bookings.isEmpty()) {
                            binding.tvEmptyBooking.setText("Anda belum memiliki riwayat sewa");
                        }
                    } else {
                        binding.tvEmptyBooking.setVisibility(View.VISIBLE);
                        binding.tvEmptyBooking.setText("Anda belum memiliki riwayat sewa");
                        Toast.makeText(getContext(), "Gagal memuat riwayat sewa", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<BookingListResponse> call, @NonNull Throwable t) {
                    if (binding == null || !isAdded()) return;
                    shimmerBooking.stopShimmer();
                    shimmerBooking.setVisibility(View.GONE);
                    binding.rvBookingHistory.setVisibility(View.VISIBLE);

                    // Jika gagal, coba load dari cache
                    String cachedJson = cacheManager.getString(CACHE_KEY_BOOKING_LIST);
                    if (cachedJson != null) {
                        List<Booking> cachedList = new Gson().fromJson(cachedJson, new TypeToken<List<Booking>>(){}.getType());
                        bookingAdapter.setBookings(cachedList);
                        binding.tvEmptyBooking.setVisibility(cachedList.isEmpty() ? View.VISIBLE : View.GONE);
                        if (cachedList.isEmpty()) {
                            binding.tvEmptyBooking.setText("Anda belum memiliki riwayat sewa");
                        }
                    } else {
                        binding.tvEmptyBooking.setVisibility(View.VISIBLE);
                        binding.tvEmptyBooking.setText("Anda belum memiliki riwayat sewa");
                        Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void loadLatestBus() {
        shimmerLatestBus.setVisibility(View.VISIBLE);
        shimmerLatestBus.startShimmer();
        binding.rvLatestBus.setVisibility(View.GONE);

        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getBuses(token)
            .enqueue(new Callback<BusListResponse>() {
                @Override
                public void onResponse(@NonNull Call<BusListResponse> call, @NonNull Response<BusListResponse> response) {
                    if (binding == null || !isAdded()) return;
                    shimmerLatestBus.stopShimmer();
                    shimmerLatestBus.setVisibility(View.GONE);
                    binding.rvLatestBus.setVisibility(View.VISIBLE);

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
                    if (binding == null || !isAdded()) return;
                    shimmerLatestBus.stopShimmer();
                    shimmerLatestBus.setVisibility(View.GONE);
                    binding.rvLatestBus.setVisibility(View.VISIBLE);

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