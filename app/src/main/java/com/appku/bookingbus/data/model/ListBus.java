package com.appku.bookingbus.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ListBus implements Parcelable {
    private int id;
    private String name;
    private String number_plate;
    private String description;
    private int default_seat_capacity;
    private String status;
    @SerializedName("images")
    private List<String> images;
    @SerializedName("main_image") 
    private String main_image;
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
    public String getMainImage() { 
        if (main_image != null && !main_image.isEmpty()) {
            return main_image;
        } else if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return "";
    }
    public String getPricing_type() { return pricing_type; }
    public String getPrice_per_day() { return price_per_day; }
    public String getPrice_per_km() { return price_per_km; }
    public String getLegrest_price_per_seat() { return legrest_price_per_seat; }

    protected ListBus(Parcel in) {
        id = in.readInt();
        name = in.readString();
        number_plate = in.readString();
        description = in.readString();
        default_seat_capacity = in.readInt();
        status = in.readString();
        images = in.createStringArrayList();
        main_image = in.readString();
        pricing_type = in.readString();
        price_per_day = in.readString();
        price_per_km = in.readString();
        legrest_price_per_seat = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(number_plate);
        dest.writeString(description);
        dest.writeInt(default_seat_capacity);
        dest.writeString(status);
        dest.writeStringList(images);
        dest.writeString(main_image);
        dest.writeString(pricing_type);
        dest.writeString(price_per_day);
        dest.writeString(price_per_km);
        dest.writeString(legrest_price_per_seat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListBus> CREATOR = new Creator<ListBus>() {
        @Override
        public ListBus createFromParcel(Parcel in) {
            return new ListBus(in);
        }

        @Override
        public ListBus[] newArray(int size) {
            return new ListBus[size];
        }
    };
}