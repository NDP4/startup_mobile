package com.appku.bookingbus.api.request;

import com.google.gson.annotations.SerializedName;

public class BookingRequest {
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
    
    @SerializedName("special_requests")
    private String specialRequests;

    // Constructor
    public BookingRequest(String bookingDate, String returnDate, String pickupLocation, 
                         String destination, int totalSeats, String seatType, String specialRequests) {
        this.bookingDate = bookingDate;
        this.returnDate = returnDate;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.seatType = seatType;
        this.specialRequests = specialRequests;
    }
}