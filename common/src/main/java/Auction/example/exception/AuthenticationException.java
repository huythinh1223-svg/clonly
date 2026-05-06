package Auction.example.exception;

import Auction.example.enums.AuthenticationError;

public class AuthenticationException extends RuntimeException {

    private final AuthenticationError ma_loi;

    // constructor cho một mã lỗi nhận vào một mã lỗi.
    public AuthenticationException ( AuthenticationError ma_loi ) {
        super(ma_loi.getMieuta());
        this.ma_loi = ma_loi;
    }

    // constructer cho admin để thêm phần bổ sung cho một lỗi
    public AuthenticationException ( AuthenticationError ma_loi , String bo_sung_loi ) {
        super(ma_loi.getMieuta() + " - " + bo_sung_loi );
        this.ma_loi = ma_loi;
    }


    // getter để lấy mã lỗi trả về
    public AuthenticationError getMa_loi() {
        return ma_loi;
    }
}
