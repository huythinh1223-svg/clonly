package Auction.example.dto;

import Auction.example.model.auction.Auction;

import java.io.Serializable;
import java.util.List;

public class AuctionListResponse implements Serializable {
    private List<Auction> auctions;
}
