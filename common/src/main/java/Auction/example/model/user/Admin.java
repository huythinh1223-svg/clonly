package Auction.example.model.user;

import Auction.example.enums.UserRole;
import Auction.example.model.auction.Auction;

public class Admin extends User {
    private int accessLevel1;

    public Admin(String id, String username, String passworld, String fullname, String email, int accessLevel1) {
        super(id, username, passworld, fullname, email, UserRole.ADMIN);
        this.accessLevel1 = accessLevel1;
    }

    // get , set accessLevel1.
    public int getAccessLevel1() {
        return accessLevel1;
    }

    public void setAccessLevel1(int accessLevel1) {
        this.accessLevel1 = accessLevel1;
    }

    // xác thực người dùng
    public void verifyUser(User newUser) {
        // xác thực dựa trên id nếu không có id thì người dùng đó không hợp lệ
        if (newUser.getId() == null) {
            System.out.println("Người dùng không hợp lệ");
            return;
        }

        // kiểm tra xem người dùng đó đã được xác thực chưa nếu rồi thì cho qua nếu chưa thì xác thực cho người dùng đó
        if (newUser.isIsverify() == true) {
            System.out.println("Người dùng: " + newUser.getFullname() + " đã được xác thực");
        }

        if (newUser.isIsverify() == false) {
            System.out.println("Người dùng: " + newUser.getFullname() + " chưa được xác thực.");
            System.out.println("Vui lòng đợi admin xác thực");
            newUser.setIsverify(true);
            System.out.println("Người dùng: " + newUser.getFullname() + " đã được xác thực bởi admin " + this.getFullname());

        }


    }

// Xóa những phiên giao dịch không hợp lệ
    public void removeInpropriateAuction (Auction auction, String reason ) {
        // Kiểm tra an toàn cho Item để tránh NullPointerException
        String itemName = "[Chưa có thông tin sản phẩm]";
        if (auction.getAuctionItem() != null) {
            itemName = auction.getAuctionItem().getName();
        }
        System.out.println("Sản phẩm: " + itemName + " đã bị admin" + this.getFullname() + " xóa do không họp lệ");
        auction.cancel(reason);
    }

// hiển thị thông tin của đối tượng admin
    @Override
    public void displayInfo() {

        System.out.println("=== THÔNG TIN ADMIN ===");
        System.out.printf("%-20s: %s%n","Tên" , getFullname());
        System.out.printf("%-20s: %s%n","Email" , getEmail());
        System.out.printf("%-20s: %s%n", "Cấp độ truy cập", "Level " + this.getAccessLevel1());
    }

// vai trò của đối tượng admin
    @Override
    public void getPermissions() {
        System.out.println("Vai trò của bạn là người quản lí hệ thống");
    }
}
