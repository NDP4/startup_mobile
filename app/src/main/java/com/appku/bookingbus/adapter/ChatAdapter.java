package com.appku.bookingbus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final List<Message> messages = new ArrayList<>();

    private static final int TYPE_MINE = 1;
    private static final int TYPE_OTHER = 2;

    public void setMessages(List<Message> newMessages) {
        messages.clear();
        if (newMessages != null) messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = messages.get(position);
        return msg.isMine() ? TYPE_MINE : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_MINE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_mine, parent, false);
            return new MineViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
            return new OtherViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        if (holder instanceof MineViewHolder) {
            ((MineViewHolder) holder).tvMessage.setText(msg.getMessage());
            ((MineViewHolder) holder).tvTime.setText(msg.getCreatedAt());
        } else if (holder instanceof OtherViewHolder) {
            ((OtherViewHolder) holder).tvMessage.setText(msg.getMessage());
            ((OtherViewHolder) holder).tvTime.setText(msg.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        MineViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageMine);
            tvTime = itemView.findViewById(R.id.tvTimeMine);
        }
    }

    static class OtherViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        OtherViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageOther);
            tvTime = itemView.findViewById(R.id.tvTimeOther);
        }
    }
}
