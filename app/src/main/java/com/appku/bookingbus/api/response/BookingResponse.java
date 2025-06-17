package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Booking;

public class BookingResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private Booking booking;
        private Payment payment;

        public static class Payment {
            private String snap_token;
            private String payment_url;

            public String getSnap_token() { return snap_token; }
            public String getPayment_url() { return payment_url; }
        }

        public Booking getBooking() { return booking; }
        public Payment getPayment() { return payment; }
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }
}