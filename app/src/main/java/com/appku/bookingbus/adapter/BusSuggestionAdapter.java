package com.appku.bookingbus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.ListBus;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class BusSuggestionAdapter extends RecyclerView.Adapter<BusSuggestionAdapter.ViewHolder> {
    private final List<ListBus> suggestions = new ArrayList<>();
    private OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(ListBus bus);
    }

    public void setSuggestions(List<ListBus> buses) {
        suggestions.clear();
        if (buses != null) suggestions.addAll(buses);
        notifyDataSetChanged();
    }

    public void setOnSuggestionClickListener(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bus_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListBus bus = suggestions.get(position);
        holder.tvBusName.setText(bus.getName());
        String imageUrl = "https://bus.ndp.my.id/storage/" + bus.getMainImage();
        Glide.with(holder.ivBusImage.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.bus_placeholder)
                .error(R.drawable.bus_placeholder)
                .centerCrop()
                .into(holder.ivBusImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSuggestionClick(bus);
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBusImage;
        TextView tvBusName;

        ViewHolder(View itemView) {
            super(itemView);
            ivBusImage = itemView.findViewById(R.id.ivBusImage);
            tvBusName = itemView.findViewById(R.id.tvBusName);
        }
    }
}
