package Auction.example.model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserManager {
    private static volatile UserManager instance;
    private final List<User> users;

    private UserManager() {
        users = Collections.synchronizedList(new ArrayList<>());
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

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        // Chặn dữ liệu trùng khi load lại từ file hoặc khi nhiều client cùng đăng ký.
        if (findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("User ID already exists: " + user.getId());
        }
        if (findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        users.add(user);
    }

    public List<User> getAllUsers() {
        synchronized (users) {
            return new ArrayList<>(users);
        }
    }

    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        synchronized (users) {
            return users.stream()
                    .filter(user -> id.equals(user.getId()))
                    .findFirst();
        }
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        synchronized (users) {
            return users.stream()
                    .filter(user -> username.equals(user.getUsername()))
                    .findFirst();
        }
    }

    public void clear() {
        users.clear();
    }

    public boolean authenticate(String username, String password) {
        synchronized (users) {
            for (User user : users) {
                if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                    return true;
                }
            }
        }
        return false;
    }
}
