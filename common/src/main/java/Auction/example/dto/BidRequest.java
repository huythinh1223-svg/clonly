package Auction.example.dto;

import Auction.example.model.user.User;
import java.io.Serializable;

public class BidRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int auctionId;
    private double amount;
    private User bidder;
}
