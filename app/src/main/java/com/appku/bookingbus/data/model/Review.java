package com.appku.bookingbus.data.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private int id;

    @SerializedName("bus")
    private BusReview bus;

    @SerializedName("booking")
    private BookingReview booking;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() { return id; }
    public BusReview getBus() { return bus; }
    public BookingReview getBooking() { return booking; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public static class BusReview {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("rating")
        private int rating;
        @SerializedName("comment")
        private String comment;

        public int getId() { return id; }
        public String getName() { return name; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }
    }

    public static class BookingReview {
        @SerializedName("id")
        private int id;
        @SerializedName("customer_name")
        private String customerName;
        @SerializedName("booking_date")
        private String bookingDate;
        @SerializedName("pickup_location")
        private String pickupLocation;
        @SerializedName("destination")
        private String destination;
        @SerializedName("status")
        private String status;

        public int getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getBookingDate() { return bookingDate; }
        public String getPickupLocation() { return pickupLocation; }
        public String getDestination() { return destination; }
        public String getStatus() { return status; }
    }
}
