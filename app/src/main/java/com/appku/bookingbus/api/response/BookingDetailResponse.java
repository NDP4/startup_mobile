package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Booking;

public class BookingDetailResponse {
    private boolean success;
    private String message;
    private Booking data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Booking getData() { return data; }
}
