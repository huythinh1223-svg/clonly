package service;
import
import Auction.example.dto.LoginRequest;
import Auction.example.dto.LoginResponse;

public class LoginService {

    public static LoginResponse login(
            LoginRequest request){

        if(request.getUsername().equals("admin")
                && request.getPassword().equals("123456")){

            return new LoginResponse(true, "Đăng nhập thành công");}

        return new LoginResponse(false, "Sai tài khoản");}
}