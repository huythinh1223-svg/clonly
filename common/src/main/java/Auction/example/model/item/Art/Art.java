package Auction.example.model.item.Art;

import Auction.example.enums.ItemCondition;
import Auction.example.model.item.items.Item;

public class Art extends Item {
    private String artistName;
    private int creationYear;
    private boolean isAuthentic;

    public Art(String id, String name, String description, double startingPrice,
               ItemCondition condition, String artistName, int creationYear, boolean isAuthentic) {
        super(id, name, description, startingPrice, condition);
        this.artistName = artistName;
        this.creationYear = creationYear;
        this.isAuthentic = isAuthentic;
    }

// getter
    public String getArtistName() {return artistName;}
    public int getCreationYear() {return creationYear;}
    public boolean isAuthentic() {return isAuthentic;}


    @Override
    public boolean validateItem() {
        return this.startingPrice > 0 && this.isAuthentic;
    }


    @Override
    public String getDisplayInfo() {
        String authStatus = isAuthentic ? "Đã chứng nhận" : "Chưa kiểm định";
        return String.format("[Nghệ thuật] %s - Tác giả: %s (%d) | Trạng thái: %s",
                name, artistName, creationYear, authStatus);
    }
}
