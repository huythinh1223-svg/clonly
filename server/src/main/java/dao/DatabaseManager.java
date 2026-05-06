package dao;

import Auction.example.model.auction.Auction;
import Auction.example.model.auction.AuctionManager;
import Auction.example.model.user.Admin;
import Auction.example.model.user.User;
import Auction.example.model.user.UserManager;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String AUCTION_DB_FILE = "auctions_db.dat";
    private static final String USER_DB_FILE = "users_db.dat"; // Thêm file lưu User

    private final FileDAO<List<Auction>> auctionDAO;
    private final FileDAO<List<User>> userDAO; // Thêm bộ đọc/ghi User

    private final AuctionManager auctionManager;
    private final UserManager userManager; // Thêm quản lý User

    public DatabaseManager() {
        this.auctionDAO = new FileDAO<>();
        this.userDAO = new FileDAO<>();
        this.auctionManager = AuctionManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    public void loadDataOnStartup() {
        System.out.println("=== ĐANG TẢI DỮ LIỆU TỪ Ổ CỨNG ===");

        // 1. Tải dữ liệu Đấu giá
        List<Auction> loadedAuctions = auctionDAO.loadFromFile(AUCTION_DB_FILE);
        if (loadedAuctions != null) {
            for (Auction a : loadedAuctions) {
                try { auctionManager.addAuction(a); } catch (Exception e) {}
            }
            System.out.println("=> Đã khôi phục " + loadedAuctions.size() + " phiên đấu giá.");
        }

        // 2. Tải dữ liệu Người dùng
        List<User> loadedUsers = userDAO.loadFromFile(USER_DB_FILE);
        if (loadedUsers != null && !loadedUsers.isEmpty()) {
            for (User u : loadedUsers) {
                userManager.addUser(u);
            }
            System.out.println("=> Đã khôi phục " + loadedUsers.size() + " tài khoản.");
        } else {
            // MẸO: Nếu Database trống (chạy lần đầu), tạo sẵn 1 tài khoản để bạn đăng nhập test
            System.out.println("=> CSDL User trống. Tạo tài khoản mặc định: admin / 123456");
            userManager.addUser(new Admin("U01", "admin", "123456", "Admin Hệ Thống", "admin@gmail.com",1));
        }
    }

    public void saveAllData() {
        System.out.println("=== ĐANG LƯU DỮ LIỆU XUỐNG Ổ CỨNG ===");

        // Lưu Auction
        List<Auction> auctionsToSave = new ArrayList<>(auctionManager.getAllAuctions());
        auctionDAO.saveToFile(auctionsToSave, AUCTION_DB_FILE);

        // Lưu User
        List<User> usersToSave = new ArrayList<>(userManager.getAllUsers());
        userDAO.saveToFile(usersToSave, USER_DB_FILE);

        System.out.println("=> Đã lưu toàn bộ dữ liệu an toàn!");
    }
}
