package com.appku.bookingbus.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.CrewAssignmentResponse;
import com.appku.bookingbus.data.model.CrewAssignment;
import com.appku.bookingbus.databinding.FragmentNotificationsBinding;
import com.appku.bookingbus.ui.chat.ChatDialogFragment;
import com.appku.bookingbus.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.appku.bookingbus.api.response.UnreadCountResponse;
import com.google.gson.Gson;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private CrewAssignmentAdapter adapter;
    private ShimmerFrameLayout shimmerNotification;
    private BadgeDrawable chatBadge;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        shimmerNotification = binding.shimmerNotification;

        adapter = new CrewAssignmentAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(assignment -> {
            // Buka detail crew assignment
            Intent intent = new Intent(getContext(), CrewAssignmentDetailActivity.class);
            intent.putExtra("assignment", new Gson().toJson(assignment));
            startActivity(intent);
        });

        // Inisialisasi chatBadge sebelum fetchUnreadCount
        chatBadge = BadgeDrawable.create(requireContext());
        chatBadge.setVisible(false);

        fetchUnreadCount();

        loadAssignments();

        // FAB membuka chat dialog
        binding.fabChat.setOnClickListener(v -> showChatDialog());

        return root;
    }

    private void loadAssignments() {
        shimmerNotification.setVisibility(View.VISIBLE);
        shimmerNotification.startShimmer();
        binding.recyclerView.setVisibility(View.GONE);

        String token = "Bearer " + new SessionManager(requireContext()).getToken();

        ApiClient.getInstance().getService().getCrewAssignments(token)
            .enqueue(new retrofit2.Callback<CrewAssignmentResponse>() {
                @Override
                public void onResponse(@NonNull Call<CrewAssignmentResponse> call, @NonNull Response<CrewAssignmentResponse> response) {
                    shimmerNotification.stopShimmer();
                    shimmerNotification.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<CrewAssignment> all = response.body().getData();
                        adapter.setAssignments(all);
                        binding.tvEmpty.setVisibility(all.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<CrewAssignmentResponse> call, @NonNull Throwable t) {
                    shimmerNotification.stopShimmer();
                    shimmerNotification.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            });
    }

    private void fetchUnreadCount() {
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getUnreadCount(token)
            .enqueue(new Callback<UnreadCountResponse>() {
                @Override
                public void onResponse(@NonNull Call<UnreadCountResponse> call, @NonNull Response<UnreadCountResponse> response) {
                    if (chatBadge == null) return;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        int count = response.body().getData().getUnreadCount();
                        if (count > 0) {
                            chatBadge.setNumber(count);
                            chatBadge.setVisible(true);
                            BadgeUtils.attachBadgeDrawable(chatBadge, binding.fabChat, null);
                        } else {
                            chatBadge.setVisible(false);
                            BadgeUtils.detachBadgeDrawable(chatBadge, binding.fabChat);
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<UnreadCountResponse> call, @NonNull Throwable t) {}
            });
    }

    private void showChatDialog() {
        new ChatDialogFragment().show(getChildFragmentManager(), "chat_dialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}