package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Bus;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BusListResponse {
    private boolean success;
    private Data data;
    private String message;

    public boolean isSuccess() { return success; }
    public Data getData() { return data; }
    public String getMessage() { return message; }

    public static class Data {
        @SerializedName("current_page")
        private int currentPage;
        private List<Bus> data;
        @SerializedName("first_page_url")
        private String firstPageUrl;
        private int from;
        @SerializedName("last_page")
        private int lastPage;
        @SerializedName("last_page_url")
        private String lastPageUrl;
        @SerializedName("next_page_url")
        private String nextPageUrl;
        private String path;
        @SerializedName("per_page")
        private int perPage;
        @SerializedName("prev_page_url")
        private String prevPageUrl;
        private int to;
        private int total;

        public List<Bus> getData() { return data; }
        // Other getters as needed
    }
}