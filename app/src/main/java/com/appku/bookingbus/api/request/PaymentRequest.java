package com.appku.bookingbus.api.request;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PaymentRequest {
    @SerializedName("booking_id")
    private int bookingId;
    @SerializedName("amount")
    private double amount;
    @SerializedName("payment_type")
    private String paymentType;
    @SerializedName("payment_details")
    private Map<String, Object> paymentDetails;

    public PaymentRequest(int bookingId, double amount, String paymentType, Map<String, Object> paymentDetails) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentDetails = paymentDetails;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Map<String, Object> getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(Map<String, Object> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}
