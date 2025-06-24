package com.appku.bookingbus.api.response;

public class UnreadCountResponse {
    private boolean success;
    private Data data;

    public static class Data {
        private int unread_count;
        public int getUnreadCount() { return unread_count; }
    }

    public boolean isSuccess() { return success; }
    public Data getData() { return data; }
}
