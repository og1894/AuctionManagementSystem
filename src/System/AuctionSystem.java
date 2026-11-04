package System;

public class AuctionSystem {
    private HashTable<Integer, AuctionLot> auctionLots;
    private HashTable<Integer, Bidder> bidders;
    private int nextLotID;
    private int nextBidderID;
    private int nextBidID;
}
