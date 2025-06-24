package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Review;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReviewListResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Review> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Review> getData() { return data; }
}
