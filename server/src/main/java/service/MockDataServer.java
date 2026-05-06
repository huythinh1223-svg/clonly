package service;
import java.util.ArrayList;
import java.util.List;

public class MockDataServer {

    // Phương thức giả lập việc lấy dữ liệu từ Database
    public static List<Product> layDanhSachSanPhamTest() {
        List<Product> products = new ArrayList<>();

        // Thêm các sản phẩm mẫu vào danh sách
        products.add(new Product("Tai nghe chống ồn Sony WH-1000XM5", 5500000.0));
        products.add(new Product("Tai nghe Bose QuietComfort 45", 6200000.0));
        products.add(new Product("Bàn phím cơ Keychron K8 Pro", 2150000.0));
        products.add(new Product("Giày chạy bộ đường dài", 1850000.0));
        products.add(new Product("Tài khoản học tiếng Anh 1 năm", 890000.0));

        return products;
    }
}