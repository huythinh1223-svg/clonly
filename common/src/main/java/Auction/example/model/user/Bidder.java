package Auction.example.model.user;

import Auction.example.enums.UserRole;
import Auction.example.model.auction.Auction;
import Auction.example.model.auction.Bid;

import java.util.ArrayList;
import java.util.List;

public class Bidder  extends  User  {
    private double balance;
    private List <Bid> bidHistory;

    public Bidder ( String id , String username , String passworld , String fullname , String email, double balance ){
        super(id , username , passworld , fullname , email , UserRole.BIDDER );
        this.balance = balance;
        bidHistory = new ArrayList<Bid>();
    }

// get,set balance
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

// get, set history
    public List<Bid> getBidHistory() {
        return bidHistory;
    }

    public void setBidHistory(List<Bid> bidHistory) {
        this.bidHistory = bidHistory;
    }

// tiến hành gia giá
    public void placeBid (Auction auction, double price ) {
        // số tiền đấu giá lớn hơn số dư của ví
        if ( price > balance ) {
            System.out.println("Lỗi: Số dư hiện tại không đủ");
            return; // nếu sai thì dừng hàm
        }

        // 2. Kiểm tra với giá hiện tại của phiên đấu giá
        double currentPrice = auction.getCurrentPrice();
        if (price <= currentPrice) {
            System.out.println("Lỗi: Giá đặt (" + price + ") thấp hơn giá cao nhất hiện tại là (" + currentPrice + ").");
            return; // mếu sai thì dừng hàm
        }

        System.out.println("Da giá: " + price);
         balance = balance - price;
    }
// cập nhập số dư nạp thêm tiền.
    public void topUPBalance (double amount ) {
        balance = balance + amount;
        System.out.println("Nạp tiền thành công số dư hiện tại của bạn là: " + balance + "VND" );

    }

// thêm lịch sử giao dịch
    public void addBidHistory(Bid history) {
        bidHistory.add(history);
    }

// override lai getInfo lay thong tin cua nguoi dau gia
    @Override
    public void displayInfo() {
        // Dùng getter của lớp cha để lấy tên, và in thêm số dư
        System.out.println("=== THÔNG TIN NGƯỜI ĐẤU GIÁ ===");
        System.out.printf("%-20s: %s%n", "Tên" , getFullname());
        System.out.printf("%-20s: %s%n", "Email" , getEmail());
        System.out.printf("%-20s: %s%n", "Số dư ví" , this.balance + " VNĐ");
    }

// overrride lai getPermisstion the hien vai tro cua nguoi do

    @Override
    public void getPermissions() {
        System.out.println("Vai trò của bạn là đấu giá sản phẩm");
    }

// tối ưu giao diện cho javaFx
// %s là để bỏ lại một khoảng trống để điền một khoảng String; \n dùng để xuống dòng sau khi điền xong
public String getFormattedDisplayInfo() {
    return String.format(
            "Vai trò: Người Đấu Giá\n" +
                    "Họ tên: %s\n" +
                    "Email: %s\n" +
                    "Số dư: %,.0f VNĐ",
            getFullname(), getEmail(), getBalance()
    );
}
}

