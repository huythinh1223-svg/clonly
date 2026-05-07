package dao;

import java.io.*;

/**
 * Class Generic dùng để lưu mọi loại danh sách (List<Auction>, List<User>...) xuống file .dat
 * Dùng Serialization
 */
public class FileDAO<T> {

    // Hàm Ghi file (Save)
    public boolean saveToFile(T data, String filePath) {
        // try-with-resources tự động đóng luồng sau khi lưu xong
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.err.println("[FileDAO LỖI] Không thể lưu file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Hàm Đọc file (Load)
    @SuppressWarnings("unchecked")
    public T loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[FileDAO INFO] File " + filePath + " chưa tồn tại. Sẽ tạo mới ở lần lưu tới.");
            return null; // Trả về null để hệ thống biết là chưa có data
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileDAO LỖI] Không thể đọc file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
