package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Booking;
import java.util.List;

public class BookingListResponse {
    private boolean success;
    private String message;
    private List<Booking> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Booking> getData() { return data; }
}
