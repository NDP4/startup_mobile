package com.appku.bookingbus.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.CrewAssignment;
import java.util.ArrayList;
import java.util.List;

public class CrewAssignmentAdapter extends RecyclerView.Adapter<CrewAssignmentAdapter.ViewHolder> {
    private final List<CrewAssignment> assignments = new ArrayList<>();

    public void setAssignments(List<CrewAssignment> list) {
        assignments.clear();
        if (list != null) assignments.addAll(list);
        notifyDataSetChanged();
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(CrewAssignment assignment);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew_assigment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewAssignment ca = assignments.get(position);
        holder.tvTitle.setText("Penugasan Baru: " + (ca.getBooking() != null ? ca.getBooking().getDestination() : "-"));
        holder.tvStatus.setText("Status: " + ca.getStatus());
        holder.tvNotes.setText("Catatan: " + (ca.getNotes() != null ? ca.getNotes() : "-"));
        holder.tvBookingDate.setText("Tanggal: " + (ca.getBooking() != null ? formatDate(ca.getBooking().getBookingDate()) : "-"));
        if (holder.tvCrewName != null) {
            holder.tvCrewName.setText(ca.getCrew() != null ? ca.getCrew().getName() : "-");
        }
        // Copy phone to clipboard on click
        if (holder.tvCrewPhone != null) {
            String phone = (ca.getCrew() != null && ca.getCrew().getPhone() != null) ? ca.getCrew().getPhone() : "-";
            holder.tvCrewPhone.setText(phone);
            holder.tvCrewPhone.setOnClickListener(v -> {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) v.getContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("phone", phone);
                clipboard.setPrimaryClip(clip);
                android.widget.Toast.makeText(v.getContext(), "Nomor disalin ke clipboard", android.widget.Toast.LENGTH_SHORT).show();
            });
        }
        // Item click for detail
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) onItemClickListener.onItemClick(ca);
        });
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    private String formatDate(String dateTime) {
        if (dateTime == null) return "-";
        try {
            return dateTime.split("T")[0];
        } catch (Exception e) {
            return dateTime;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvStatus, tvNotes, tvBookingDate, tvCrewName, tvCrewPhone;
        ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvStatus = v.findViewById(R.id.tvStatus);
            tvNotes = v.findViewById(R.id.tvNotes);
            tvBookingDate = v.findViewById(R.id.tvBookingDate);
            tvCrewName = v.findViewById(R.id.tvCrewName);
            tvCrewPhone = v.findViewById(R.id.tvCrewPhone);
        }
    }
}
