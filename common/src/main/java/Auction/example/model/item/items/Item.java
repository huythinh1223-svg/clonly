package Auction.example.model.item.items;

import Auction.example.enums.ItemCondition;

public abstract class Item {
    protected String id;
    protected String name;
    protected String description; // mô tả
    protected double startingPrice;
    protected ItemCondition condition; // loại sản phẩm

    public Item(String id, String name, String description, double startingPrice, ItemCondition condition) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.condition = condition;
    }

// getter
    public String getId() {return id;}
    public String getName() {
        return name;
    }
    public double getStartingPrice() {
        return startingPrice;
    }
    public ItemCondition getCondition() {
        return condition;
    }


// tạo giá khởi điểm
    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

// hiển thị thông tin sản phẩm
    public abstract String getDisplayInfo();

// kiểm tra tính hợp lệ của sản phẩm
    public abstract boolean validateItem();

}
