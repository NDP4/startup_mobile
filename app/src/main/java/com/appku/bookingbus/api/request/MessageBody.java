package com.appku.bookingbus.api.request;

public class MessageBody {
    private String message;
    private int receiver_id;

    public MessageBody(String message, int receiver_id) {
        this.message = message;
        this.receiver_id = receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public int getReceiver_id() {
        return receiver_id;
    }
}
