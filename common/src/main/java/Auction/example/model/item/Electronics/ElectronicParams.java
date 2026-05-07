package Auction.example.model.item.Electronics;

import Auction.example.enums.ItemCondition;
import Auction.example.model.item.items.Item;
import Auction.example.model.item.items.ItemParams;


public class ElectronicParams extends ItemParams {

    private int warryantyPeroid; // thời gian bảo hành
    private String brand; // hãng sản xuất.

    public ElectronicParams ( int warryantyPeroid , String brand ) {
        this.warryantyPeroid = warryantyPeroid;
        this.brand = brand;
    }


// getter
    public int getWarryantyPeroid()  {return warryantyPeroid;}
    public String getBrand()  {return brand;}
}
