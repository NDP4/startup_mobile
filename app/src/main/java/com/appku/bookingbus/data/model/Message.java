package com.appku.bookingbus.data.model;

public class Message {
    private int id;
    private String message;
    private boolean is_mine;
    private String created_at;
    private User sender;
    private User receiver;

    public int getId() { return id; }
    public String getMessage() { return message; }
    public boolean isMine() { return is_mine; }
    public String getCreatedAt() { return created_at; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }

    public static class User {
        private int id;
        private String name;
        private String role;
        public int getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }
}
