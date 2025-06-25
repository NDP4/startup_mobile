package com.appku.bookingbus.ui.notifications;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.CrewAssignment;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CrewAssignmentDetailActivity extends AppCompatActivity {
    private static String formatDate(String dateTime) {
        if (dateTime == null) return "-";
        try {
            // Format ISO 8601: 2025-05-10T17:00:00.000000Z
            String date = dateTime.split("T")[0];
            return date;
        } catch (Exception e) {
            return dateTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crew_assignment_detail);

        String json = getIntent().getStringExtra("assignment");
        CrewAssignment assignment = new Gson().fromJson(json, CrewAssignment.class);

        ((TextView) findViewById(R.id.tvAssignmentId)).setText("" + assignment.getId());
        ((TextView) findViewById(R.id.tvStatus)).setText("" + assignment.getStatus());
        ((TextView) findViewById(R.id.tvNotes)).setText("" + (assignment.getNotes() != null ? assignment.getNotes() : "-"));

        // Crew Info
        TextView tvCrewName = findViewById(R.id.tvCrewName);
        TextView tvCrewPhone = findViewById(R.id.tvCrewPhone);
        ImageButton btnCopyPhone = findViewById(R.id.btnCopyPhone);

        if (assignment.getCrew() != null) {
            tvCrewName.setText("" + assignment.getCrew().getName());
            tvCrewPhone.setText("" + assignment.getCrew().getPhone());

            btnCopyPhone.setOnClickListener(v -> {
                String phone = assignment.getCrew().getPhone();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone", phone);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Nomor telepon disalin ke clipboard", Toast.LENGTH_SHORT).show();
            });
        } else {
            tvCrewName.setText("");
            tvCrewPhone.setText("");
            btnCopyPhone.setEnabled(false);
        }

        // Booking Info
        if (assignment.getBooking() != null) {
            ((TextView) findViewById(R.id.tvBookingId)).setText("" + assignment.getBooking().getId());
            ((TextView) findViewById(R.id.tvDestination)).setText("" + assignment.getBooking().getDestination());
            ((TextView) findViewById(R.id.tvPickupLocation)).setText("" + assignment.getBooking().getPickupLocation());
            ((TextView) findViewById(R.id.tvBookingDate)).setText("" + formatDate(assignment.getBooking().getBookingDate()));
            ((TextView) findViewById(R.id.tvReturnDate)).setText("" + formatDate(assignment.getBooking().getReturnDate()));
            ((TextView) findViewById(R.id.tvTotalSeats)).setText("" + assignment.getBooking().getTotalSeats());
            ((TextView) findViewById(R.id.tvSeatType)).setText("" + assignment.getBooking().getSeatType());
            ((TextView) findViewById(R.id.tvTotalAmount)).setText(formatCurrency(assignment.getBooking().getTotalAmount()));
            ((TextView) findViewById(R.id.tvSpecialRequests)).setText("" + (assignment.getBooking().getSpecialRequests() != null ? assignment.getBooking().getSpecialRequests() : "-"));
        }
    }

    private static String formatCurrency(double amount) {
        return String.format(new Locale("id", "ID"), "Rp %,d", (long)amount);
    }
}
