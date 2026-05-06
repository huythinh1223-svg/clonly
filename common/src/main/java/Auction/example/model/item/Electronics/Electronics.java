package Auction.example.model.item.Electronics;

import Auction.example.enums.ItemCondition;
import Auction.example.model.item.items.Item;

public class Electronics extends Item {
    private int warrantyPeriod;
    private String brand;

    public Electronics(String id, String name, String description, double startingPrice,
                       ItemCondition condition, int warrantyPeriod, String brand) {
        super(id, name, description, startingPrice, condition);
        this.warrantyPeriod = warrantyPeriod;
        this.brand = brand;
    }

// getter
    public int getWarrantyPeriod() {return warrantyPeriod;}
    public String getBrand() {return brand;}



    @Override
    public boolean validateItem() {
        return this.startingPrice > 0 && this.warrantyPeriod >= 0;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("[Điện tử] %s - Hãng: %s | Tình trạng: %s | Bảo hành: %d tháng",
                name, brand, condition, warrantyPeriod);
    }
}
