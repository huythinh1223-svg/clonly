package Auction.example.enums;

public enum AuthenticationError {
    USER_NOT_FOUND ("Không tìm thấy tên người dùng"),
    INVALID_CREDENTIALS ("Sai tên đăng nhập hoặc mật khẩu"),
    ACCOUNT_LOCKED ("Tài khaonr của bạn đã bị khóa"),
    TOKEN_EXPIRED ("Hết phiên đăng nhập");

    private final String mieuta;

    private AuthenticationError (String mieuta ) {
        this.mieuta = mieuta;
    }

    public String getMieuta() {
        return this.mieuta;
    }
}


