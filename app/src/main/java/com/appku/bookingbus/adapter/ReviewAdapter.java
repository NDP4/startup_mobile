package com.appku.bookingbus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.appku.bookingbus.R;
import com.appku.bookingbus.data.model.Review;
import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviews = new ArrayList<>();

    public void setReviews(List<Review> list) {
        reviews.clear();
        if (list != null) reviews.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvBusName.setText(review.getBus() != null ? review.getBus().getName() : "-");
        holder.tvCustomerName.setText(review.getBooking() != null ? review.getBooking().getCustomerName() : "-");
        holder.tvBookingDate.setText(review.getBooking() != null ? review.getBooking().getBookingDate() : "-");
        holder.tvComment.setText(review.getBus() != null ? review.getBus().getComment() : "-");
        holder.tvRating.setText(String.valueOf(review.getBus() != null ? review.getBus().getRating() : 0));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBusName, tvCustomerName, tvBookingDate, tvComment, tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            tvBusName = itemView.findViewById(R.id.tvBusName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
