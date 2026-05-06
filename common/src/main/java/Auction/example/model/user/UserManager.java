package Auction.example.model.user;

import Auction.example.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    // --- Singleton Pattern ---
    private static volatile UserManager instance;
    private final List<User> users;

    private UserManager() {
        users = new ArrayList<>();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    // --- Các hàm xử lý ---
    public void addUser(User user) {
        users.add(user);
    }

    public List<User> getAllUsers() {
        return users;
    }

    // Hàm kiểm tra đăng nhập thực tế
    public boolean authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return true; // Khớp cả user và pass
            }
        }
        return false; // Không tìm thấy hoặc sai pass
    }
}
