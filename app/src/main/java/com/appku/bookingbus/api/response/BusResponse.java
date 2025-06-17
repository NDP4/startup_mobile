package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.DetailBus;

public class BusResponse {
    private boolean success;
    private String message;
    private DetailBus data;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public DetailBus getData() { return data; }
    public void setData(DetailBus data) { this.data = data; }
}