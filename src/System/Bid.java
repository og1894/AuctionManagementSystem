package System;

import java.time.LocalDateTime;

public class Bid {
    private int bidID;
    private Bidder bidder;
    private AuctionLot auctionLot;
    private double amount;
    private LocalDateTime dateTime;
    private boolean withdrawn;
    private boolean winningBid;
}
