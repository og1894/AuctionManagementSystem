package System;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BidTest {
    private Bidder bidder;
    private AuctionLot auctionLot;
    private Bid bid;
    private Bid lowerBid;
    private Bid higherBid;
    private Bid earlierBid;
    private Bid laterBid;
    private LocalDateTime bidDateTime;

    @BeforeEach
    void setUp() {
        bidder = new Bidder(1001, "John Smith", "Waterford City", "0871234567", "john@example.com");

        auctionLot = new AuctionLot(2001, "Victorian Mahogany Chair", "Carved Victorian dining chair", "Furniture", 1880, 450.00);

        bidDateTime = LocalDateTime.of(2027, 1, 10, 12, 30);

        bid = new Bid(3001, bidder, auctionLot, 500.00, bidDateTime);

        lowerBid = new Bid(3002, bidder, auctionLot, 450.00, LocalDateTime.of(2027, 1, 10, 11, 30));
        higherBid = new Bid(3003, bidder, auctionLot, 550.00, LocalDateTime.of(2027, 1, 10, 13, 30));

        laterBid = new Bid(3005, bidder, auctionLot, 575.00, LocalDateTime.of(2027, 1, 10, 14, 0));
        earlierBid = new Bid(3004, bidder, auctionLot, 475.00, LocalDateTime.of(2027, 1, 10, 10, 0));
    }

    @AfterEach
    void tearDown() {
        bidder = null;
        auctionLot = null;
        bid = null;
        lowerBid = null;
        higherBid = null;
        earlierBid = null;
        laterBid = null;
        bidDateTime = null;
    }

    @Test
    void getBidID() {
        assertEquals(3001, bid.getBidID());
    }

    @Test
    void getBidder() {
        assertSame(bidder, bid.getBidder());
    }

    @Test
    void getAuctionLot() {
        assertSame(auctionLot, bid.getAuctionLot());
    }

    @Test
    void getAmount() {
        assertEquals(500.00, bid.getAmount(), 0.001);
    }

    @Test
    void getDateTime() {
        assertEquals(LocalDateTime.of(2027, 1, 10, 12, 30), bid.getDateTime());
    }

    @Test
    void isWithdrawn() {
        assertFalse(bid.isWithdrawn());
        bid.withdrawBid();
        assertTrue(bid.isWithdrawn());
    }

    @Test
    void isWinningBid() {
        assertFalse(bid.isWinningBid());
        bid.markAsWinningBid();
        assertTrue(bid.isWinningBid());
    }

    @Test
    void isActive() {
        assertTrue(bid.isActive());
    }

    @Test
    void withdrawBid() {
        bid.withdrawBid();
        assertAll(() -> assertTrue(bid.isWithdrawn()), () -> assertFalse(bid.isWinningBid()), () -> assertFalse(bid.isActive()), () -> assertEquals("WITHDRAWN", bid.getStatusDescription()));
    }

    @Test
    void markAsWinningBid() {
        bid.markAsWinningBid();
        assertAll(() -> assertTrue(bid.isWinningBid()), () -> assertFalse(bid.isWithdrawn()), () -> assertFalse(bid.isActive()), () -> assertEquals("WINNING", bid.getStatusDescription()));
    }

    @Test
    void getStatusDescription() {
        assertEquals("ACTIVE", bid.getStatusDescription());
    }

    @Test
    void isHigherThan() {
        assertTrue(bid.isHigherThan(lowerBid));
    }

    @Test
    void wasMadeBefore() {
        assertTrue(bid.wasMadeBefore(laterBid));
    }

    @Test
    void wasMadeAfter() {
        assertTrue(bid.wasMadeAfter(earlierBid));
    }

    @Test
    void testToString() {
        String result = bid.toString();
        assertAll(() -> assertTrue(result.contains("Bid ID: 3001")), () -> assertTrue(result.contains("Bidder ID: 1001")), () -> assertTrue(result.contains("Bidder name: John Smith")), () -> assertTrue(result.contains("Auction lot ID: 2001")), () -> assertTrue(result.contains("Auction lot title: " + "Victorian Mahogany Chair")), () -> assertTrue(result.contains("Bid amount: €500.0")), () -> assertTrue(result.contains("Bid date/time: " + "2027-01-10T12:30")), () -> assertTrue(result.contains("Status: ACTIVE")));
    }
}