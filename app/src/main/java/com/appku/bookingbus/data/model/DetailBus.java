package com.appku.bookingbus.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DetailBus implements Parcelable {
    private int id;
    private String name;
    private String number_plate;
    private String description;
    private int default_seat_capacity;
    private String status;
    private String pricing_type;
    private String price_per_day;
    private String price_per_km;
    private String legrest_price_per_seat;

    public static class Image {
        @SerializedName("url")
        private String url;
        @SerializedName("description")
        private String description;

        public String getUrl() { return url; }
        public String getDescription() { return description; }
    }

    @SerializedName("images")
    private List<Image> images;
    @SerializedName("main_image")
    private String main_image;

    // Getter methods
    public int getId() { return id; }
    public String getName() { return name; }
    public String getNumber_plate() { return number_plate; }
    public String getDescription() { return description; }
    public int getDefault_seat_capacity() { return default_seat_capacity; }
    public String getStatus() { return status; }
    public List<Image> getImages() { return images; }
    public String getMain_image() { return main_image; }
    public String getPricing_type() { return pricing_type; }
    public String getPrice_per_day() { return price_per_day; }
    public String getPrice_per_km() { return price_per_km; }
    public String getLegrest_price_per_seat() { return legrest_price_per_seat; }

    protected DetailBus(Parcel in) {
        id = in.readInt();
        name = in.readString();
        number_plate = in.readString();
        description = in.readString();
        default_seat_capacity = in.readInt();
        status = in.readString();
        main_image = in.readString();
        pricing_type = in.readString();
        price_per_day = in.readString();
        price_per_km = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(number_plate);
        dest.writeString(description);
        dest.writeInt(default_seat_capacity);
        dest.writeString(status);
        dest.writeString(main_image);
        dest.writeString(pricing_type);
        dest.writeString(price_per_day);
        dest.writeString(price_per_km);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DetailBus> CREATOR = new Creator<DetailBus>() {
        @Override
        public DetailBus createFromParcel(Parcel in) {
            return new DetailBus(in);
        }

        @Override
        public DetailBus[] newArray(int size) {
            return new DetailBus[size];
        }
    };
}