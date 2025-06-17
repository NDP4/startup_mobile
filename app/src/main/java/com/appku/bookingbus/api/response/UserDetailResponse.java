// UserDetailResponse.java
package com.appku.bookingbus.api.response;

public class UserDetailResponse {
    private boolean success;
    private String message;
    private UserData data;

    public static class UserData {
        private String name;
        private String email;
        private String phone;
        private String address;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public UserData getData() { return data; }
    public void setData(UserData data) { this.data = data; }
}