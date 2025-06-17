package com.appku.bookingbus.data.model;

import com.google.gson.annotations.SerializedName;

public class Booking {
    private int id;
    private Customer customer;
    private Bus bus;
    @SerializedName("booking_date")
    private String bookingDate;
    @SerializedName("return_date")
    private String returnDate;
    @SerializedName("pickup_location")
    private String pickupLocation;
    private String destination;
    @SerializedName("total_seats")
    private int totalSeats;
    @SerializedName("seat_type")
    private String seatType;
    @SerializedName("total_amount")
    private double totalAmount;
    @SerializedName("special_requests")
    private String specialRequests;
    private String status;
    @SerializedName("payment_status")
    private String paymentStatus;
    @SerializedName("snap_token")
    private String snapToken;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public static class Customer {
        private int id;
        private String name;
        private String email;
        private String phone;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
    }

    public static class Bus {
        private int id;
        private String name;
        @SerializedName("number_plate")
        private String numberPlate;
        @SerializedName("pricing_type")
        private String pricingType;
        @SerializedName("price_per_day")
        private double pricePerDay;
        @SerializedName("price_per_km")
        private double pricePerKm;
        @SerializedName("legrest_price_per_seat")
        private double legrestPricePerSeat;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getNumberPlate() { return numberPlate; }
        public String getPricingType() { return pricingType; }
        public double getPricePerDay() { return pricePerDay; }
        public double getPricePerKm() { return pricePerKm; }
        public double getLegrestPricePerSeat() { return legrestPricePerSeat; }
    }

    // Getters
    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Bus getBus() { return bus; }
    public String getBookingDate() { return bookingDate; }
    public String getReturnDate() { return returnDate; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDestination() { return destination; }
    public int getTotalSeats() { return totalSeats; }
    public String getSeatType() { return seatType; }
    public double getTotalAmount() { return totalAmount; }
    public String getSpecialRequests() { return specialRequests; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getSnapToken() { return snapToken; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}