package Auction.example.model.item.Art;

import Auction.example.model.item.items.ItemParams;

public class ArtParams extends ItemParams  {
    private String artist;
    private int year;
    private boolean auth; // tính chính hãng của sản phẩm

    public ArtParams ( String artist , int year , boolean auth ) {
        this.artist = artist;
        this.year = year;
        this.auth = auth;
    }


 // getter
    public String getArtist()  {return artist;}
    public int getYear() {return year;}
    public boolean isAuth() {return auth;}
}
