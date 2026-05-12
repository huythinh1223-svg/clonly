package Auction.example.dto;

import java.io.Serializable;
import java.util.List;

public class AuctionListResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Auction> auctions;
}
