package com.appku.bookingbus.ui.booking;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appku.bookingbus.data.model.Booking;
import com.appku.bookingbus.databinding.ItemBookingHistoryBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {
    private final List<Booking> bookings = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Booking booking);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<Booking> newBookings) {
        bookings.clear();
        if (newBookings != null) bookings.addAll(newBookings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingHistoryBinding binding = ItemBookingHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingHistoryBinding binding;

        ViewHolder(ItemBookingHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(bookings.get(getAdapterPosition()));
                }
            });
        }

        void bind(Booking booking) {
            binding.tvBusName.setText(booking.getBus().getName());
            binding.tvBookingDate.setText(formatDate(booking.getBookingDate()));
            // Ubah status agar kapitalisasi rapi
            String status = booking.getStatus();
            binding.tvStatus.setText(status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase());
            binding.tvTotalAmount.setText(String.format(Locale.getDefault(), "Rp %,d", (long) booking.getTotalAmount()));
        }

        private String formatDate(String dateStr) {
            try {
                return dateStr.substring(0, 10);
            } catch (Exception e) {
                return dateStr;
            }
        }
    }
}
