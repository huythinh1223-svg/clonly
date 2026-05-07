package Auction.example.model.user;

import Auction.example.enums.UserRole;
import Auction.example.model.auction.Auction;
import Auction.example.model.item.items.Item;

import java.util.ArrayList;
import java.util.List;

public class Seller extends User {
    private List<Item> ownedItems;

    public Seller(String id, String username, String passworld, String fullname, String email) {
        super(id, username, passworld, fullname, email, UserRole.SELLER);
        ownedItems = new ArrayList<Item>();
    }

    public List<Item> getOwnedItems() {
        return ownedItems;
    }

    public void setOwnedItems(List<Item> ownedItems) {
        this.ownedItems = ownedItems;
    }

    public void postItem(Item newItem) {
        // nếu không có sản phẩm nào được thêm vào thì báo lỗi và dừng.
        if (newItem == null) {
            System.out.println("Lỗi: không có món hàng nào được thêm");
            return;
        }
        // trường hợp  món hàng hợp lệ thì thêm vào danh sách.
        ownedItems.add(newItem);
        System.out.println("Thêm thành công: " + newItem.getName());
    }

    public void manageAuction(Auction auction) {

        // kiểm tra xem người bán có đúng không kiểm tra thông qua ID
        if (!auction.getSellerId().equals(this.getId())) {
            System.out.println("Người bán " + this.getFullname() + " không phải người bán của phiên giao dịch này.");
            return;
        }

        // phần quản hiển thị lí trung phiên đấu giá
        System.out.println("=========================================");
        System.out.println("           QUẢN LÝ PHIÊN ĐẤU GIÁ         ");
        System.out.println("=========================================");

        // In ra ID phiên đấu giá
        System.out.printf("%-22s: %s%n", "Mã phiên", auction.getAuctionId());

        // Đi từ Auction -> Lấy đối tượng Item -> Lấy Tên Item
        //%-22s : từ phía bên trái bỏ ra ô phần trống gốm 22 ô để đền một chuỗi String
        // : dấu ':" xẽ xuất hiện ở ô trống thứ 23 để căn thẳng tất cả các phần
        // %s%n : %s phía bên phải bỏ ra số ô tùy ý để điền thông tin sản phẩm; %n xuống dòng sau khi hết.
        // Nếu Item bị null, báo lỗi thay vì để chương trình crash

        if (auction.getAuctionItem() != null) {
            System.out.printf("%-22s: %s%n", "Tên sản phẩm", auction.getAuctionItem().getName()); // Đã thêm ngoặc đóng
        } else {
            System.out.printf("%-22s: %s%n", "Tên sản phẩm", "[Chưa có thông tin sản phẩm]");
        }

        // cập nhập trạng thái của sản phẩm này đã đấu giá thành công hay chưa đấu giá.
        System.out.printf("%-22s: %s%n", "Trạng thái", auction.getState());

        // In ra giá cao nhất, định dạng %,.0f có dấu phẩy ngăn cách hàng nghìn
        System.out.printf("%-22s: %,.0f VNĐ%n", "Giá cao nhất hiện tại", auction.getCurrentPrice());

        System.out.println("=========================================");

// nếu trạng thái giao dịch vẫn đang là sẵn sàng mà thời gian đã hết thì phải gọi hàm xử lí kết quả
        // Đã đồng bộ logic kiểm tra trạng thái và thời gian với class Auction
        if (auction.getState() == Auction.State.RUNNING && auction.getRemainingTimeMillis() <= 0) {
            auction.finish(); // Đã đổi closeAuction() thành finish()
            System.out.println("Phiên đấu giá đã kết thúc do hết thời gian.");
        }
    }

    @Override
    public void displayInfo() {
        // Dùng getter của lớp cha để lấy tên, và in thêm số dư
        System.out.println("=== THÔNG TIN NGƯỜI BÁN ĐẤU GIÁ ===");
        System.out.printf("%-20s: %s%n","Tên" , getFullname());
        System.out.printf("%-20s: %s%n","Email" , getEmail());
        System.out.printf("%-20s: %s%n", "Số sản phẩm còn lại" , ownedItems.size() + "sản phẩm");
    }

    @Override
    public void getPermissions() {
        System.out.println("Vai trò của bạn là người bán và điều hành đấu giá cho sản phẩm");
    }
}
