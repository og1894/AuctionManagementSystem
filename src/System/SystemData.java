package System;

public class SystemData {
    private AuctionSystem auctionSystem;

    public SystemData(AuctionSystem auctionSystem) {
        if (auctionSystem == null) {
            throw new IllegalArgumentException("Auction system cannot be null.");
        }
        this.auctionSystem = auctionSystem;
    }

    public AuctionSystem getAuctionSystem() {
        return auctionSystem;
    }

    public void setAuctionSystem(AuctionSystem auctionSystem) {
        if (auctionSystem == null) {
            throw new IllegalArgumentException("Auction system cannot be null.");
        }
        this.auctionSystem = auctionSystem;
    }
}