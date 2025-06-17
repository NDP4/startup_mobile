package com.appku.bookingbus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.Bus;
import com.appku.bookingbus.databinding.ItemBusBinding;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private final List<Bus> buses = new ArrayList<>();
    private final Context context;
    private OnItemClickListener listener;

    public BusAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setBuses(List<Bus> newBuses) {
        buses.clear();
        buses.addAll(newBuses);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBusBinding binding = ItemBusBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = buses.get(position);
        holder.bind(bus);
    }

    @Override
    public int getItemCount() {
        return buses.size();
    }

    class BusViewHolder extends RecyclerView.ViewHolder {
        private final ItemBusBinding binding;

        BusViewHolder(ItemBusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(buses.get(position));
                }
            });
        }

        void bind(Bus bus) {
            // Set bus name and number plate
            binding.tvBusName.setText(bus.getName());
            binding.tvCapacity.setText("Plate: " + bus.getNumber_plate() + " • " + bus.getDefault_seat_capacity() + " Seats");
            
            // Format price based on pricing type
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String price;
            if (bus.getPricing_type().equals("daily")) {
                price = formatter.format(Double.parseDouble(bus.getPrice_per_day())) + "/day";
            } else {
                price = formatter.format(Double.parseDouble(bus.getPrice_per_km())) + "/km";
            }
            binding.tvPrice.setText(price);

            // Set status chip color and text
            String status = bus.getStatus().substring(0, 1).toUpperCase() + bus.getStatus().substring(1).toLowerCase();
            binding.chipStatus.setText(status);
            
            int chipColor;
            switch (bus.getStatus().toLowerCase()) {
                case "available":
                    chipColor = R.color.primary;
                    break;
                case "maintenance":
                    chipColor = android.R.color.holo_orange_dark;
                    break;
                case "booked":
                    chipColor = android.R.color.holo_red_light;
                    break;
                default:
                    chipColor = android.R.color.darker_gray;
                    break;
            }
            binding.chipStatus.setChipBackgroundColorResource(chipColor);

            // Load image dari URL lengkap
            String imageUrl = "https://bus.ndp.my.id/storage/" + bus.getMainImage();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.bus_placeholder)
                    .error(R.drawable.bus_placeholder)
                    .centerCrop()
                    .into(binding.ivBus);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Bus bus);
    }
}