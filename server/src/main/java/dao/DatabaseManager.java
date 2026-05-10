package dao;

import Auction.example.enums.ItemCondition;
import Auction.example.model.auction.Auction;
import Auction.example.model.auction.AuctionManager;
import Auction.example.model.item.Electronics.Electronics;
import Auction.example.model.user.Admin;
import Auction.example.model.user.Bidder;
import Auction.example.model.user.Seller;
import Auction.example.model.user.User;
import Auction.example.model.user.UserManager;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String AUCTION_DB_FILE = "auctions_db.dat";
    private static final String USER_DB_FILE = "users_db.dat";

    private final FileDAO<List<Auction>> auctionDAO;
    private final FileDAO<List<User>> userDAO;

    private final AuctionManager auctionManager;
    private final UserManager userManager;

    public DatabaseManager() {
        this.auctionDAO = new FileDAO<>();
        this.userDAO = new FileDAO<>();
        this.auctionManager = AuctionManager.getInstance();
        this.userManager = UserManager.getInstance();
    }

    public synchronized void loadDataOnStartup() {
        System.out.println("=== Loading database from disk ===");
        // Load file lên RAM trước, sau đó tạo dữ liệu mẫu nếu file chưa có dữ liệu.
        loadUsers();
        loadAuctions();
        ensureDefaultUsers();
        ensureDefaultAuctions();
        System.out.println("=> Users: " + userManager.getAllUsers().size());
        System.out.println("=> Auctions: " + auctionManager.getAllAuctions().size());
    }

    public synchronized void saveAllData() {
        saveAuctions();
        saveUsers();
        System.out.println("=> Database saved.");
    }

    public synchronized void saveUsers() {
        userDAO.saveToFile(new ArrayList<>(userManager.getAllUsers()), USER_DB_FILE);
    }

    public synchronized void saveAuctions() {
        auctionDAO.saveToFile(new ArrayList<>(auctionManager.getAllAuctions()), AUCTION_DB_FILE);
    }

    public synchronized void addUser(User user) {
        userManager.addUser(user);
        saveUsers();
    }

    public synchronized void addAuction(Auction auction) {
        auctionManager.addAuction(auction);
        saveAuctions();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    private void loadUsers() {
        // Xóa dữ liệu RAM hiện tại để tránh nhân đôi khi hàm load bị gọi lại.
        userManager.clear();
        List<User> loadedUsers = userDAO.loadFromFile(USER_DB_FILE);
        if (loadedUsers == null) {
            return;
        }

        for (User user : loadedUsers) {
            try {
                userManager.addUser(user);
            } catch (IllegalArgumentException e) {
                System.err.println("[Database] Skip duplicate user: " + e.getMessage());
            }
        }
    }

    private void loadAuctions() {
        // Auction có timer/observer transient nên cần restoreAfterLoad sau khi đọc từ file.
        auctionManager.clear();
        List<Auction> loadedAuctions = auctionDAO.loadFromFile(AUCTION_DB_FILE);
        if (loadedAuctions == null) {
            return;
        }

        for (Auction auction : loadedAuctions) {
            try {
                auction.restoreAfterLoad();
                auctionManager.addAuction(auction);
            } catch (IllegalArgumentException e) {
                System.err.println("[Database] Skip duplicate auction: " + e.getMessage());
            }
        }
    }

    private void ensureDefaultUsers() {
        addUserIfMissing(new Admin("U01", "admin", "123456", "Admin He Thong", "admin@gmail.com", 1));
        addUserIfMissing(new Bidder("U02", "nguoimua", "1111", "Tran Khach Hang", "mua@gmail.com", 5000000.0));
        addUserIfMissing(new Seller("U03", "nguoiban", "2222", "Cua hang Do Co", "ban@gmail.com"));
    }

    private void addUserIfMissing(User user) {
        boolean exists = userManager.findById(user.getId()).isPresent()
                || userManager.findByUsername(user.getUsername()).isPresent();
        if (!exists) {
            userManager.addUser(user);
        }
    }

    private void ensureDefaultAuctions() {
        if (!auctionManager.getAllAuctions().isEmpty()) {
            return;
        }

        Electronics headphones = new Electronics(
                "I01",
                "Tai nghe Sony WH-1000XM5",
                "Tai nghe chong on",
                5500000.0,
                ItemCondition.USED,
                6,
                "Sony"
        );
        Auction firstAuction = new Auction("AUC01", "U03", headphones, 5500000.0, 120, 100000.0);
        auctionManager.addAuction(firstAuction);

        Electronics keyboard = new Electronics(
                "I02",
                "Ban phim co Keychron K2",
                "Ban phim co khong day",
                2150000.0,
                ItemCondition.USED,
                3,
                "Keychron"
        );
        Auction secondAuction = new Auction("AUC02", "U03", keyboard, 2150000.0, 90, 50000.0);
        auctionManager.addAuction(secondAuction);
    }
}
