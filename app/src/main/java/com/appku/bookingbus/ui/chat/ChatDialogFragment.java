package com.appku.bookingbus.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.appku.bookingbus.R;
import com.appku.bookingbus.api.ApiClient;
import com.appku.bookingbus.api.response.ChatResponse;
import com.appku.bookingbus.api.response.SendMessageResponse;
import com.appku.bookingbus.data.model.Message;
import com.appku.bookingbus.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.appku.bookingbus.adapter.ChatAdapter;

public class ChatDialogFragment extends BottomSheetDialogFragment {
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadMessages();
            handler.postDelayed(this, 2000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chat, container, false);
        rvChat = view.findViewById(R.id.rvChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter();
        // Set stackFromEnd true so newest messages are at the bottom (start scroll at bottom)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        // Jangan gunakan setReverseLayout(true) agar urutan tetap lama ke baru, tapi scroll di bawah
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);

        loadMessages();

        btnSend.setOnClickListener(v -> {
            String msg = etMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
            }
        });

        // Mulai auto-refresh saat dialog terbuka
        handler.post(refreshRunnable);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hentikan auto-refresh saat dialog ditutup
        handler.removeCallbacks(refreshRunnable);
    }

    private void loadMessages() {
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        ApiClient.getInstance().getService().getChatMessages(token)
            .enqueue(new Callback<ChatResponse>() {
                @Override
                public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Message> messages = response.body().getData();
                        chatAdapter.setMessages(messages);
                        // Always scroll to the bottom (newest)
                        if (messages != null && !messages.isEmpty()) {
                            rvChat.scrollToPosition(messages.size() - 1);
                        }
                    } else {
                        android.util.Log.e("ChatDialogFragment", "Load messages failed: " + response.code());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                    android.util.Log.e("ChatDialogFragment", "Load messages error: ", t);
                }
            });
    }

    private void sendMessage(String message) {
        String token = "Bearer " + new SessionManager(requireContext()).getToken();
        // Cari id admin dari pesan terakhir, fallback ke 1 jika tidak ada
        int receiverId = 1;
        List<Message> messages = chatAdapter != null ? chatAdapter.messages : null;
        if (messages != null && !messages.isEmpty()) {
            for (Message msg : messages) {
                if (msg.getReceiver() != null && "admin".equals(msg.getReceiver().getRole())) {
                    receiverId = msg.getReceiver().getId();
                    break;
                }
                if (msg.getSender() != null && "admin".equals(msg.getSender().getRole())) {
                    receiverId = msg.getSender().getId();
                    break;
                }
            }
        }
        ApiClient.getInstance().getService().sendChatMessage(token, new com.appku.bookingbus.api.request.MessageBody(message, receiverId))
            .enqueue(new Callback<SendMessageResponse>() {
                @Override
                public void onResponse(@NonNull Call<SendMessageResponse> call, @NonNull Response<SendMessageResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        etMessage.setText("");
                        loadMessages();
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            errorBody = "Error reading errorBody: " + e.getMessage();
                        }
                        android.util.Log.e("ChatDialogFragment", "Send message failed: " + response.code() + " | " + errorBody);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<SendMessageResponse> call, @NonNull Throwable t) {
                    android.util.Log.e("ChatDialogFragment", "Send message error: ", t);
                }
            });
    }
}
