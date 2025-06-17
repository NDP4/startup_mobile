package com.appku.bookingbus.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Bus {
    private int id;
    private String name;
    private String number_plate;
    private String description;
    private int default_seat_capacity;
    private String status;
    private List<String> images;
    private String pricing_type;
    private String price_per_day;
    private String price_per_km;
    private String legrest_price_per_seat;

    // Getter methods
    public int getId() { return id; }
    public String getName() { return name; }
    public String getNumber_plate() { return number_plate; }
    public String getDescription() { return description; }
    public int getDefault_seat_capacity() { return default_seat_capacity; }
    public String getStatus() { return status; }
    public List<String> getImages() { return images; }
    public String getPricing_type() { return pricing_type; }
    public String getPrice_per_day() { return price_per_day; }
    public String getPrice_per_km() { return price_per_km; }
    public String getLegrest_price_per_seat() { return legrest_price_per_seat; }
    
    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return "";
    }
}