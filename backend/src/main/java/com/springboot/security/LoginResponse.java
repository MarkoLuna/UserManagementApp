package com.springboot.security;

public class LoginResponse {
    private String token;
    private String type;
    private long expiresIn;
    
    public LoginResponse() {
        this.type = "Bearer";
        this.expiresIn = 864000000; // 10 days in milliseconds
    }
    
    public LoginResponse(String token) {
        this();
        this.token = token;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
