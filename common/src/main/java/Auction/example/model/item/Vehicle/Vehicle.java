package Auction.example.model.item.Vehicle;

import Auction.example.enums.ItemCondition;
import Auction.example.model.item.items.Item;

public class Vehicle extends Item {
    private int mileage;
    private String engineType;
    private String licensePlate;

    public Vehicle(String id, String name, String description, double startingPrice,
                   ItemCondition condition, int mileage, String engineType, String licensePlate) {
        super(id, name, description, startingPrice, condition);
        this.mileage = mileage;
        this.engineType = engineType;
        this.licensePlate = licensePlate;
    }



    @Override
    public boolean validateItem() {
        if (this.condition == ItemCondition.NEW && this.mileage > 0) {
            return false;
        }
        return this.startingPrice > 0 && this.mileage >= 0;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("[Xe cộ] %s - Động cơ: %s | Biển số: %s | Đã đi: %d km",
                name, engineType, licensePlate, mileage);
    }
}
