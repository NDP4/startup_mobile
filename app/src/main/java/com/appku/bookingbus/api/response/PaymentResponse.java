package com.appku.bookingbus.api.response;

public class PaymentResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String paymentUrl;

        public String getPaymentUrl() {
            return paymentUrl;
        }

        public void setPaymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}
