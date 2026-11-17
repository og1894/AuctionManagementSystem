package System;

import java.time.LocalDateTime;

public class Bid {
    private final int bidID;
    private final Bidder bidder;
    private final AuctionLot auctionLot;
    private final double amount;
    private final LocalDateTime dateTime;
    private boolean withdrawn;
    private boolean winningBid;

    public Bid(int bidID, Bidder bidder, AuctionLot auctionLot, double amount) {
        this(bidID, bidder, auctionLot, amount, LocalDateTime.now());
    }

    public Bid(int bidID, Bidder bidder, AuctionLot auctionLot, double amount, LocalDateTime dateTime) {
        if (bidID < 1 || bidID > 999999) {
            throw new IllegalArgumentException("Bid ID must be between 1 and 999999.");
        }

        if (bidder == null) {
            throw new IllegalArgumentException("A bid must have a bidder.");
        }

        if (auctionLot == null) {
            throw new IllegalArgumentException("A bid must refer to an auction lot.");
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Bid amount must be greater than zero.");
        }

        if (dateTime == null) {
            throw new IllegalArgumentException("Bid date and time cannot be null.");
        }

        this.bidID = bidID;
        this.bidder = bidder;
        this.auctionLot = auctionLot;
        this.amount = amount;
        this.dateTime = dateTime;
        withdrawn = false;
        winningBid = false;
    }

    public int getBidID() {
        return bidID;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public AuctionLot getAuctionLot() {
        return auctionLot;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public boolean isWinningBid() {
        return winningBid;
    }

    public boolean isActive() {
        return !withdrawn && !winningBid;
    }

    public void withdrawBid() {
        if (withdrawn) {
            throw new IllegalStateException("The bid has already been withdrawn.");
        }

        if (winningBid) {
            throw new IllegalStateException("An accepted winning bid cannot be withdrawn.");
        }

        if (auctionLot.isSold()) {
            throw new IllegalStateException("A bid on a sold auction lot cannot be withdrawn.");
        }
        withdrawn = true;
    }

    void markAsWinningBid() {
        if (withdrawn) {
            throw new IllegalStateException("A withdrawn bid cannot be accepted.");
        }

        if (winningBid) {
            throw new IllegalStateException("The bid has already been accepted.");
        }
        winningBid = true;
    }

    public String getStatusDescription() {
        if (winningBid) {
            return "WINNING";
        }

        if (withdrawn) {
            return "WITHDRAWN";
        }
        return "ACTIVE";
    }

    public boolean isHigherThan(Bid otherBid) {
        return otherBid == null || amount > otherBid.amount;
    }

    public boolean wasMadeBefore(Bid otherBid) {
        if (otherBid == null) {
            throw new IllegalArgumentException("Other bid cannot be null.");
        }
        return dateTime.isBefore(otherBid.dateTime);
    }

    public boolean wasMadeAfter(Bid otherBid) {
        if (otherBid == null) {
            throw new IllegalArgumentException("Other bid cannot be null.");
        }
        return dateTime.isAfter(otherBid.dateTime);
    }

    @Override
    public String toString() {
        return "Bid ID: " + bidID + "\nBidder ID: " + bidder.getBidderID() + "\nBidder name: " + bidder.getName() + "\nAuction lot ID: " + auctionLot.getLotID() + "\nAuction lot title: " + auctionLot.getTitle() + "\nBid amount: €" + amount + "\nBid date/time: " + dateTime + "\nStatus: " + getStatusDescription();
    }
}