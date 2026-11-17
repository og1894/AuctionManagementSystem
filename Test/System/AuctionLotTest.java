package System;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AuctionLotTest {
    private AuctionLot auctionLot;
    private Bidder firstBidder;
    private Bidder secondBidder;
    private Bidder thirdBidder;
    private Bid firstBid;
    private Bid secondBid;
    private Bid thirdBid;

    @BeforeEach
    void setUp() {
        auctionLot = new AuctionLot(1001, "Victorian Mahogany Chair", "Carved Victorian dining chair", "Furniture", 1880, 450.00);

        firstBidder = new Bidder(2001, "John Smith", "Waterford City", "0871234567", "john@example.com");
        secondBidder = new Bidder(2002, "Mary Murphy", "Tramore, Waterford", "0861234567", "mary@example.com");
        thirdBidder = new Bidder(2003, "Sarah Jones", "Dungarvan, Waterford", "0851234567", "sarah@example.com");

        firstBid = new Bid(3001, firstBidder, auctionLot, 500.00, LocalDateTime.of(2027, 1, 10, 10, 0));
        secondBid = new Bid(3002, secondBidder, auctionLot, 550.00, LocalDateTime.of(2027, 1, 10, 11, 0));
        thirdBid = new Bid(3003, thirdBidder, auctionLot, 600.00, LocalDateTime.of(2027, 1, 10, 12, 0));
    }


    @AfterEach
    void tearDown() {
        auctionLot = null;
        firstBidder = null;
        secondBidder = null;
        thirdBidder = null;
        firstBid = null;
        secondBid = null;
        thirdBid = null;
    }

    @Test
    void getLotID() {
        assertEquals(1001, auctionLot.getLotID());
    }

    @Test
    void getTitle() {
        assertEquals("Victorian Mahogany Chair", auctionLot.getTitle());
    }

    @Test
    void setTitle() {
        auctionLot.setTitle("Victorian Dining Chair");
        assertEquals("Victorian Dining Chair", auctionLot.getTitle());
    }

    @Test
    void getDescription() {
        assertEquals("Carved Victorian dining chair", auctionLot.getDescription());
    }

    @Test
    void setDescription() {
        auctionLot.setDescription("Restored carved mahogany chair");
        assertEquals("Restored carved mahogany chair", auctionLot.getDescription());
    }

    @Test
    void getType() {
        assertEquals("Furniture", auctionLot.getType());
    }

    @Test
    void setType() {
        auctionLot.setType("Antique Furniture");
        assertEquals("Antique Furniture", auctionLot.getType());
    }

    @Test
    void getEstimatedYear() {
        assertEquals(1880, auctionLot.getEstimatedYear());
    }

    @Test
    void setEstimatedYear() {
        auctionLot.setEstimatedYear(1890);
        assertEquals(1890, auctionLot.getEstimatedYear());
    }

    @Test
    void getGuidePrice() {
        assertEquals(450.00, auctionLot.getGuidePrice(), 0.001);
    }

    @Test
    void setGuidePrice() {
        auctionLot.setGuidePrice(500.00);
        assertEquals(500.00, auctionLot.getGuidePrice(), 0.001);
    }

    @Test
    void editDetails() {
        auctionLot.editDetails("Edwardian Chair", "Edwardian oak dining chair", "Furniture", 1910, 525.00);
        assertAll(() -> assertEquals("Edwardian Chair", auctionLot.getTitle()), () -> assertEquals("Edwardian oak dining chair", auctionLot.getDescription()), () -> assertEquals("Furniture", auctionLot.getType()), () -> assertEquals(1910, auctionLot.getEstimatedYear()), () -> assertEquals(525.00,auctionLot.getGuidePrice(), 0.001));
    }

    @Test
    void isSold() {
        assertFalse(auctionLot.isSold());
        addFirstBid();
        auctionLot.sellLot();
        assertTrue(auctionLot.isSold());
    }

    @Test
    void isWithdrawn() {
        assertFalse(auctionLot.isWithdrawn());
        auctionLot.withdrawLot();
        assertTrue(auctionLot.isWithdrawn());
    }

    @Test
    void isAvailable() {
        assertTrue(auctionLot.isAvailable());
    }

    @Test
    void getSaleDateTime() {
        assertNull(auctionLot.getSaleDateTime());
        addFirstBid();
        auctionLot.sellLot();
        assertNotNull(auctionLot.getSaleDateTime());
    }

    @Test
    void getFinalPrice() {
        assertEquals(0, auctionLot.getFinalPrice(), 0.001);
        addFirstBid();
        auctionLot.sellLot();
        assertEquals(500.00, auctionLot.getFinalPrice(), 0.001);
    }

    @Test
    void getStatusDescription() {
        assertEquals("AVAILABLE", auctionLot.getStatusDescription());
    }

    @Test
    void withdrawLot() {
        auctionLot.withdrawLot();
        assertAll(() -> assertTrue(auctionLot.isWithdrawn()), () -> assertFalse(auctionLot.isAvailable()), () -> assertFalse(auctionLot.isSold()));
    }

    @Test
    void getBids() {
        assertNotNull(auctionLot.getBids());
        assertTrue(auctionLot.getBids().isEmpty());
    }

    @Test
    void getNumberOfBids() {
        assertEquals(0, auctionLot.getNumberOfBids());
        addFirstBid();
        assertEquals(1, auctionLot.getNumberOfBids());
    }

    @Test
    void addBid() {
        boolean added = auctionLot.addBid(firstBid);
        assertAll(() -> assertTrue(added), () -> assertEquals(1, auctionLot.getNumberOfBids()), () -> assertSame(firstBid, auctionLot.getBids().get(0)));
    }

    @Test
    void findBid() {
        addFirstBid();
        auctionLot.addBid(secondBid);
        assertSame(secondBid, auctionLot.findBid(3002));
    }

    @Test
    void getHighestBid() {
        addThreeBids();
        assertSame(thirdBid, auctionLot.getHighestBid());
    }

    @Test
    void getNumberOfActiveBids() {
        addThreeBids();
        assertEquals(3, auctionLot.getNumberOfActiveBids());
    }

    @Test
    void sellLot() {
        addThreeBids();
        LocalDateTime beforeSale = LocalDateTime.now();
        auctionLot.sellLot();
        LocalDateTime afterSale = LocalDateTime.now();
        assertAll(() -> assertTrue(auctionLot.isSold()), () -> assertFalse(auctionLot.isAvailable()), () -> assertFalse(auctionLot.isWithdrawn()), () -> assertEquals(600.00, auctionLot.getFinalPrice(), 0.001), () -> assertTrue(thirdBid.isWinningBid()), () -> assertFalse(firstBid.isWinningBid()), () -> assertFalse(secondBid.isWinningBid()), () -> assertNotNull(auctionLot.getSaleDateTime()), () -> assertFalse(auctionLot.getSaleDateTime().isBefore(beforeSale)), () -> assertFalse(auctionLot.getSaleDateTime().isAfter(afterSale)));
    }

    @Test
    void getBidsSortedByAmountDescending() {
        addThreeBids();
        LinkedList<Bid> sorted = auctionLot.getBidsSortedByAmountDescending();
        assertAll(() -> assertEquals(3, sorted.size()), () -> assertSame(thirdBid, sorted.get(0)), () -> assertSame(secondBid, sorted.get(1)), () -> assertSame(firstBid, sorted.get(2)));
    }

    @Test
    void matchesSearch() {
        assertTrue(auctionLot.matchesSearch("mahogany", "Furniture", 1880, "dining"));
    }

    @Test
    void getDetailedReport() {
        addThreeBids();
        String report = auctionLot.getDetailedReport();
        int thirdBidPosition = report.indexOf("Bid ID: 3003");
        int secondBidPosition = report.indexOf("Bid ID: 3002");
        int firstBidPosition = report.indexOf("Bid ID: 3001");

        assertAll(() -> assertTrue(report.contains("Auction lot ID: 1001")), () -> assertTrue(report.contains("BIDS - HIGHEST FIRST")), () -> assertTrue(report.contains("Bid ID: 3001")), () -> assertTrue(report.contains("Bid ID: 3002")), () -> assertTrue(report.contains("Bid ID: 3003")), () -> assertTrue(thirdBidPosition < secondBidPosition), () -> assertTrue(secondBidPosition < firstBidPosition));
    }

    @Test
    void testToString() {
        String result = auctionLot.toString();
        assertAll(() -> assertTrue(result.contains("Auction lot ID: 1001")), () -> assertTrue(result.contains("Title: Victorian Mahogany Chair")), () -> assertTrue(result.contains("Description: Carved Victorian " + "dining chair")), () -> assertTrue(result.contains("Type: Furniture")), () -> assertTrue(result.contains("Estimated year: 1880")), () -> assertTrue(result.contains("Guide price: €450.0")), () -> assertTrue(result.contains("Status: AVAILABLE")), () -> assertTrue(result.contains("Number of bids: 0")));
    }

    private void addFirstBid() {
        auctionLot.addBid(firstBid);
    }

    private void addThreeBids() {
        auctionLot.addBid(firstBid);
        auctionLot.addBid(secondBid);
        auctionLot.addBid(thirdBid);
    }
}