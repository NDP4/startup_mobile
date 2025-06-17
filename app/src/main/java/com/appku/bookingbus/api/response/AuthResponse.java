// AuthResponse.java
package com.appku.bookingbus.api.response;

public class AuthResponse {
    private boolean success;
    private String message;
    private Data data;

    public static class Data {
        private String user;
        private String email;
        private String token;

        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
}