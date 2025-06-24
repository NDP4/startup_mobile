package com.appku.bookingbus.api.response;

import com.appku.bookingbus.data.model.Message;
import java.util.List;

public class ChatResponse {
    private boolean success;
    private String message;
    private List<Message> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Message> getData() { return data; }
}
