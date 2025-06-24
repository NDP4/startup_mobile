package com.appku.bookingbus.data.model;

import com.google.gson.annotations.SerializedName;

public class CrewAssignment {
    @SerializedName("id")
    private int id;

    @SerializedName("booking")
    private Booking booking;

    @SerializedName("crew")
    private Crew crew;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    public int getId() { return id; }
    public Booking getBooking() { return booking; }
    public Crew getCrew() { return crew; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public static class Crew {
        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;
        @SerializedName("phone")
        private String phone;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }
    }
}
