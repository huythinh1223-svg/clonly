package Auction.example.dto;

import Auction.example.model.user.User;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    private String message;
    private User user;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
