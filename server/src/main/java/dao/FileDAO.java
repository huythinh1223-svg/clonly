package dao;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileDAO<T> {
    public boolean saveToFile(T data, String filePath) {
        Path targetPath = Path.of(filePath);
        Path tempPath = targetPath.resolveSibling(targetPath.getFileName() + ".tmp");

        try {
            Path parent = targetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(tempPath.toFile()))) {
                outputStream.writeObject(data);
            }

            // Ghi qua file tạm trước, rồi thay thế file thật để giảm rủi ro hỏng database khi app tắt đột ngột.
            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicMoveError) {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[FileDAO ERROR] Cannot save " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public T loadFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            System.out.println("[FileDAO INFO] " + filePath + " has no data yet.");
            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (T) inputStream.readObject();
        } catch (EOFException e) {
            System.out.println("[FileDAO INFO] " + filePath + " is empty.");
            return null;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileDAO ERROR] Cannot load " + filePath + ": " + e.getMessage());
            return null;
        }
    }
}
