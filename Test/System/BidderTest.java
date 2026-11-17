package System;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BidderTest {
    private Bidder bidder;
    private Bidder secondBidder;
    private AuctionLot auctionLot;
    private Bid earliestBid;
    private Bid middleBid;
    private Bid newestBid;

    @BeforeEach
    void setUp() {
        bidder = new Bidder(1001, "John Smith", "Waterford City", "0871234567", "john@example.com");
        secondBidder = new Bidder(1002, "Mary Murphy", "Tramore, Waterford", "0861234567", "mary@example.com");

        auctionLot = new AuctionLot(2001, "Victorian Mahogany Chair", "Carved Victorian dining chair", "Furniture", 1880, 450.00);

        earliestBid = new Bid(3001, bidder, auctionLot, 500.00, LocalDateTime.of(2027, 1, 10, 10, 0));
        middleBid = new Bid(3002, bidder, auctionLot, 550.00, LocalDateTime.of(2027, 1, 10, 11, 0));
        newestBid = new Bid(3003, bidder, auctionLot, 600.00, LocalDateTime.of(2027, 1, 10, 12, 0));
    }

    @AfterEach
    void tearDown() {
        bidder = null;
        secondBidder = null;
        auctionLot = null;
        earliestBid = null;
        middleBid = null;
        newestBid = null;
    }

    @Test
    void getBidderID() {
        assertEquals(1001, bidder.getBidderID());
    }

    @Test
    void getName() {
        assertEquals("John Smith", bidder.getName());
    }

    @Test
    void setName() {
        bidder.setName("James Murphy");
        assertEquals("James Murphy", bidder.getName());
    }

    @Test
    void getAddress() {
        assertEquals("Waterford City", bidder.getAddress());
    }

    @Test
    void setAddress() {
        bidder.setAddress("Dungarvan, County Waterford");
        assertEquals("Dungarvan, County Waterford", bidder.getAddress());
    }

    @Test
    void getTelephone() {
        assertEquals("0871234567", bidder.getTelephone());
    }

    @Test
    void setTelephone() {
        bidder.setTelephone("0897654321");
        assertEquals("0897654321", bidder.getTelephone());
    }

    @Test
    void getEmail() {
        assertEquals("john@example.com", bidder.getEmail());
    }

    @Test
    void setEmail() {
        bidder.setEmail("john.smith@example.com");
        assertEquals("john.smith@example.com", bidder.getEmail());
    }

    @Test
    void editDetails() {
        bidder.editDetails("James Murphy", "Dungarvan, County Waterford", "0897654321", "james@example.com");
        assertAll(() -> assertEquals("James Murphy", bidder.getName()), () -> assertEquals("Dungarvan, County Waterford", bidder.getAddress()), () -> assertEquals("0897654321", bidder.getTelephone()), () -> assertEquals("james@example.com", bidder.getEmail()));
    }

    @Test
    void getBids() {
        assertNotNull(bidder.getBids());
        assertTrue(bidder.getBids().isEmpty());
        assertEquals(0, bidder.getBids().size());
    }

    @Test
    void getNumberOfBids() {
        assertEquals(0, bidder.getNumberOfBids());
        bidder.addBid(earliestBid);
        bidder.addBid(middleBid);
        assertEquals(2, bidder.getNumberOfBids());
    }

    @Test
    void addBid() {
        boolean added = bidder.addBid(earliestBid);
        assertAll(() -> assertTrue(added), () -> assertEquals(1, bidder.getNumberOfBids()), () -> assertSame(earliestBid, bidder.getBids().get(0)));
    }

    @Test
    void findBid() {
        addAllBids();
        assertSame(middleBid, bidder.findBid(3002));
    }

    @Test
    void withdrawBid() {
        bidder.addBid(earliestBid);
        boolean withdrawn = bidder.withdrawBid(3001);
        assertAll(() -> assertTrue(withdrawn), () -> assertTrue(earliestBid.isWithdrawn()), () -> assertSame(earliestBid, bidder.findBid(3001)));
    }

    @Test
    void getNumberOfActiveBids() {
        addAllBids();
        assertEquals(3, bidder.getNumberOfActiveBids());
    }

    @Test
    void getNumberOfWithdrawnBids() {
        addAllBids();
        earliestBid.withdrawBid();
        middleBid.withdrawBid();
        assertEquals(2, bidder.getNumberOfWithdrawnBids());
    }

    @Test
    void getNumberOfWinningBids() {
        bidder.addBid(earliestBid);
        auctionLot.addBid(earliestBid);
        auctionLot.sellLot();
        assertEquals(1, bidder.getNumberOfWinningBids());
    }

    @Test
    void hasWinningBid() {
        bidder.addBid(earliestBid);
        auctionLot.addBid(earliestBid);
        auctionLot.sellLot();
        assertTrue(bidder.hasWinningBid());
    }

    @Test
    void getBidsSortedByDateAscending() {
        bidder.addBid(newestBid);
        bidder.addBid(earliestBid);
        bidder.addBid(middleBid);

        LinkedList<Bid> sorted = bidder.getBidsSortedByDateAscending();

        assertAll(() -> assertEquals(3, sorted.size()), () -> assertSame(earliestBid, sorted.get(0)), () -> assertSame(middleBid, sorted.get(1)), () -> assertSame(newestBid, sorted.get(2)));
    }

    @Test
    void getBidsSortedByDateDescending() {
        bidder.addBid(earliestBid);
        bidder.addBid(newestBid);
        bidder.addBid(middleBid);

        LinkedList<Bid> sorted = bidder.getBidsSortedByDateDescending();

        assertAll(() -> assertEquals(3, sorted.size()), () -> assertSame(newestBid, sorted.get(0)), () -> assertSame(middleBid, sorted.get(1)), () -> assertSame(earliestBid, sorted.get(2)));
    }

    @Test
    void nameContains() {
        assertTrue(bidder.nameContains("John"));
    }

    @Test
    void addressContains() {
        assertTrue(bidder.addressContains("Waterford"));
    }

    @Test
    void matchesSearch() {
        assertTrue(bidder.matchesSearch("John", "Waterford"));
    }

    @Test
    void getDetailedReport() {
        bidder.addBid(newestBid);
        bidder.addBid(middleBid);
        bidder.addBid(earliestBid);

        String report = bidder.getDetailedReport();

        int earliestPosition = report.indexOf("Bid ID: 3001");
        int middlePosition = report.indexOf("Bid ID: 3002");
        int newestPosition = report.indexOf("Bid ID: 3003");

        assertAll(() -> assertTrue(report.contains("Bidder ID: 1001")), () -> assertTrue(report.contains("Number of bids: 3")), () -> assertTrue(report.contains("Active bids: 3")), () -> assertTrue(report.contains("Withdrawn bids: 0")), () -> assertTrue(report.contains("Winning bids: 0")), () -> assertTrue(report.contains("BID HISTORY")), () -> assertTrue(earliestPosition < middlePosition), () -> assertTrue(middlePosition < newestPosition));
    }

    @Test
    void testToString() {
        String result = bidder.toString();
        assertAll(() -> assertTrue(result.contains("Bidder ID: 1001")), () -> assertTrue(result.contains("Name: John Smith")), () -> assertTrue(result.contains("Address: Waterford City")), () -> assertTrue(result.contains("Telephone: 0871234567")), () -> assertTrue(result.contains("Email: john@example.com")));
    }

    private void addAllBids() {
        bidder.addBid(earliestBid);
        bidder.addBid(middleBid);
        bidder.addBid(newestBid);
    }
}