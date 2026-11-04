package System;

import java.time.LocalDateTime;

public class AuctionLot {
    private int lotID;
    private String title;
    private String description;
    private String type;
    private int estimatedYear;
    private double guidePrice;
    private boolean sold;
    private boolean withdrawn;
    private LocalDateTime saleDateTime;
    private double finalPrice;
    private LinkedList<Bid> bids;
}
