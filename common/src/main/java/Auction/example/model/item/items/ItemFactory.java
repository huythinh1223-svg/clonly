package Auction.example.model.item.items;

import Auction.example.enums.ItemCondition;
import Auction.example.enums.ItemType;
import Auction.example.model.item.Art.Art;
import Auction.example.model.item.Art.ArtParams;
import Auction.example.model.item.Electronics.ElectronicParams;
import Auction.example.model.item.Electronics.Electronics;
import Auction.example.model.item.Vehicle.Vehicle;
import Auction.example.model.item.Vehicle.VehicleParams;

import java.util.Map;
import java.util.UUID;

public class ItemFactory {

    public static Item createItem(ItemType type, String name, String desc, double price,
                                  ItemCondition condition, ItemParams params) {
        String generatedId = UUID.randomUUID().toString();
        Item newItem = null;


        switch (type) {
            case ELECTRONICS:
                // kiểm tra params trước khi ép kiểu.
                if ( (params instanceof ElectronicParams) == false) {
                    throw new IllegalArgumentException("Sai params cho ELECTRONICS");
                }
                ElectronicParams e = (ElectronicParams) params;

                newItem = new Electronics(generatedId, name, desc, price, condition,
                        e.getWarryantyPeroid(), e.getBrand());
                break;

            case ART:
                if (!(params instanceof ArtParams)) {
                    throw new IllegalArgumentException("Sai params cho ART");
                }
                ArtParams a = (ArtParams) params;

                newItem = new Art(generatedId, name, desc, price, condition,
                        a.getArtist(), a.getYear(), a.isAuth() );
                break;

            case VEHICLE:
                if (!(params instanceof VehicleParams)) {
                    throw new IllegalArgumentException("Sai params cho VEHICLE");
                }

                VehicleParams v = (VehicleParams) params;
                newItem = new Vehicle(generatedId, name, desc, price, condition,
                        v.getMileage(), v.getEnginerType(), v.getLicensePlate() );
                break;

// nếu loại sản phẩm tạo ra không có trong danh sách ta báo lỗi
            default:
                throw new IllegalArgumentException("Loại sản phẩm không được hỗ trợ: " + type);
        }

        if (newItem.validateItem() == false) {
            throw new IllegalArgumentException("Thông số sản phẩm không hợp lệ, không thể khởi tạo!");
        }

        return newItem;
    }
}