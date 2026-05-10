package Auction.example.interfaces;

public interface WalletService {

    // Giữ tiền để bid
    // available -= amount
    // reserved += amount
    boolean reserveFunds(String userId, double amount);

    // Trả lại tiền đã giữ
    // available += amount
    // reserved -= amount
    void releaseFunds(String userId, double amount);

    // Trừ tiền thật khi auction kết thúc
    // balance -= amount
    // reserved -= amount
    void captureFunds(String userId, double amount);

    // Nạp tiền
    void deposit(String userId, double amount);

    // Rút tiền
    boolean withdraw(String userId, double amount);

    // Tổng tiền hiện có
    double getBalance(String userId);

    // Tiền còn dùng được
    double getAvailableBalance(String userId);

    // Tiền đang bị giữ
    double getReservedBalance(String userId);
}