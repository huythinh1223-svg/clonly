package Auction.example.dto;

import Auction.example.model.user.User;
import java.io.Serializable;

public class BidRequest implements Serializable {
    private int auctionId;
    private double amount;
    private User bidder;
}
