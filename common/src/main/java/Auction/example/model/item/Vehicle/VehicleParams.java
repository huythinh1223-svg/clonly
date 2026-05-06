package Auction.example.model.item.Vehicle;

import Auction.example.model.item.items.ItemParams;

public class VehicleParams extends ItemParams {
    private int  mileage; // số dặm
    private String enginerType; // loại động cơ
    private String licensePlate; // biển số xe

    public VehicleParams ( int mileage , String enginerType , String licensePlate ) {
        this.mileage = mileage;
        this.enginerType = enginerType;
        this.licensePlate = licensePlate;
    }

    // getter
    public int getMileage() {return mileage;}
    public String getEnginerType() {return enginerType;}
    public String getLicensePlate() {return licensePlate;}
}
