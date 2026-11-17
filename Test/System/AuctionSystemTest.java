package System;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionSystemTest {
    private AuctionSystem auctionSystem;
    private AuctionLot chair;
    private AuctionLot vase;
    private AuctionLot painting;
    private Bidder john;
    private Bidder alice;
    private Bidder mary;

    @BeforeEach
    void setUp() {
        auctionSystem = new AuctionSystem();

        chair = auctionSystem.addAuctionLot("Victorian Mahogany Chair", "Carved Victorian dining chair", "Furniture", 1880, 450.00);
        vase = auctionSystem.addAuctionLot("Blue Ceramic Vase", "Hand-painted blue ceramic vase", "Ceramics", 1920, 300.00);
        painting = auctionSystem.addAuctionLot("Landscape Painting", "Oil painting of the Irish countryside", "Art", 1750, 800.00);

        john = auctionSystem.addBidder("John Smith", "Waterford City", "0871234567", "john@example.com");
        alice = auctionSystem.addBidder("Alice Brown", "Cork City", "0861234567", "alice@example.com");
        mary = auctionSystem.addBidder("Mary Murphy", "Dublin City", "0851234567", "mary@example.com");
    }

    @AfterEach
    void tearDown() {
        auctionSystem = null;
        chair = null;
        vase = null;
        painting = null;
        john = null;
        alice = null;
        mary = null;
    }

    @Test
    void addAuctionLot() {
        AuctionLot newLot = auctionSystem.addAuctionLot("Antique Clock", "Victorian mantel clock", "Clocks", 1890, 350.00);

        assertAll(() -> assertNotNull(newLot), () -> assertEquals(4, newLot.getLotID()), () -> assertEquals("Antique Clock", newLot.getTitle()), () -> assertEquals("Victorian mantel clock", newLot.getDescription()), () -> assertEquals("Clocks", newLot.getType()), () -> assertEquals(1890, newLot.getEstimatedYear()), () -> assertEquals(350.00, newLot.getGuidePrice(), 0.001), () -> assertEquals(4, auctionSystem.getNumberOfAuctionLots()), () -> assertSame(newLot, auctionSystem.findAuctionLot(4)));
    }

    @Test
    void findAuctionLot() {
        AuctionLot result = auctionSystem.findAuctionLot(chair.getLotID());
        assertSame(chair, result);
    }

    @Test
    void editAuctionLot() {
        boolean edited = auctionSystem.editAuctionLot(chair.getLotID(), "Edwardian Dining Chair", "Restored Edwardian oak chair", "Furniture", 1910, 525.00);

        assertAll(() -> assertTrue(edited), () -> assertEquals("Edwardian Dining Chair", chair.getTitle()), () -> assertEquals("Restored Edwardian oak chair", chair.getDescription()), () -> assertEquals("Furniture", chair.getType()), () -> assertEquals(1910, chair.getEstimatedYear()), () -> assertEquals(525.00, chair.getGuidePrice(), 0.001));
    }

    @Test
    void withdrawAuctionLot() {
        boolean withdrawn = auctionSystem.withdrawAuctionLot(chair.getLotID());

        assertAll(() -> assertTrue(withdrawn), () -> assertTrue(chair.isWithdrawn()), () -> assertFalse(chair.isAvailable()), () -> assertEquals("WITHDRAWN", chair.getStatusDescription()));
    }

    @Test
    void sellAuctionLot() {
        Bid firstBid = auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        Bid highestBid = auctionSystem.placeBid(chair.getLotID(), mary.getBidderID(), 550);
        boolean sold = auctionSystem.sellAuctionLot(chair.getLotID());

        assertAll(() -> assertTrue(sold), () -> assertTrue(chair.isSold()), () -> assertFalse(chair.isAvailable()), () -> assertEquals(550.00, chair.getFinalPrice(), 0.001), () -> assertNotNull(chair.getSaleDateTime()), () -> assertTrue(highestBid.isWinningBid()), () -> assertFalse(firstBid.isWinningBid()));
    }

    @Test
    void addBidder() {
        Bidder newBidder = auctionSystem.addBidder("David Jones", "Kilkenny City", "0831234567", "david@example.com");
        assertAll(() -> assertNotNull(newBidder), () -> assertEquals(4, newBidder.getBidderID()), () -> assertEquals("David Jones", newBidder.getName()), () -> assertEquals("Kilkenny City", newBidder.getAddress()), () -> assertEquals("0831234567", newBidder.getTelephone()), () -> assertEquals("david@example.com", newBidder.getEmail()), () -> assertEquals(4, auctionSystem.getNumberOfBidders()), () -> assertSame(newBidder, auctionSystem.findBidder(4)));
    }

    @Test
    void findBidder() {
        Bidder result = auctionSystem.findBidder(john.getBidderID());
        assertSame(john, result);
    }

    @Test
    void editBidder() {
        boolean edited = auctionSystem.editBidder(john.getBidderID(), "John Murphy", "Tramore, Waterford", "0897654321", "john.murphy@example.com");

        assertAll(() -> assertTrue(edited), () -> assertEquals("John Murphy", john.getName()), () -> assertEquals("Tramore, Waterford", john.getAddress()), () -> assertEquals("0897654321", john.getTelephone()), () -> assertEquals("john.murphy@example.com", john.getEmail()));
    }

    @Test
    void placeBid() {
        Bid bid = auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);

        assertAll(() -> assertNotNull(bid), () -> assertEquals(1, bid.getBidID()), () -> assertSame(john, bid.getBidder()), () -> assertSame(chair, bid.getAuctionLot()), () -> assertEquals(500.00, bid.getAmount(), 0.001), () -> assertSame(bid, chair.findBid(1)), () -> assertSame(bid, john.findBid(1)), () -> assertSame(bid, auctionSystem.findBid(1)), () -> assertEquals(1, auctionSystem.getNumberOfBids()));
    }

    @Test
    void findBid() {
        Bid bid = auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        assertSame(bid, auctionSystem.findBid(1));
    }

    @Test
    void withdrawBid() {
        Bid bid = auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        boolean withdrawn = auctionSystem.withdrawBid(bid.getBidID());
        assertAll(() -> assertTrue(withdrawn), () -> assertTrue(bid.isWithdrawn()), () -> assertFalse(bid.isActive()), () -> assertSame(bid, auctionSystem.findBid(bid.getBidID())));
    }

    @Test
    void searchAuctionLots() {
        LinkedList<AuctionLot> results = auctionSystem.searchAuctionLots("chair", "Furniture", 1880, "Victorian");
        assertAll(() -> assertEquals(1, results.size()), () -> assertSame(chair, results.get(0)));
    }

    @Test
    void searchBidders() {
        LinkedList<Bidder> results = auctionSystem.searchBidders("John", "Waterford");
        assertAll(() -> assertEquals(1, results.size()), () -> assertSame(john, results.get(0)));
    }

    @Test
    void sortLotsByTitleAscending() {
        LinkedList<AuctionLot> source = createUnsortedLotList();
        LinkedList<AuctionLot> sorted = auctionSystem.sortLotsByTitleAscending(source);
        assertAll(() -> assertSame(vase, sorted.get(0)), () -> assertSame(painting, sorted.get(1)), () -> assertSame(chair, sorted.get(2)));
    }

    @Test
    void sortLotsByYearAscending() {
        LinkedList<AuctionLot> sorted = auctionSystem.sortLotsByYearAscending(createUnsortedLotList());
        assertAll(() -> assertSame(painting, sorted.get(0)), () -> assertSame(chair, sorted.get(1)), () -> assertSame(vase, sorted.get(2)));
    }

    @Test
    void sortLotsByYearDescending() {
        LinkedList<AuctionLot> sorted = auctionSystem.sortLotsByYearDescending(createUnsortedLotList());
        assertAll(() -> assertSame(vase, sorted.get(0)), () -> assertSame(chair, sorted.get(1)), () -> assertSame(painting, sorted.get(2)));
    }

    @Test
    void sortLotsByTypeAscending() {
        LinkedList<AuctionLot> sorted = auctionSystem.sortLotsByTypeAscending(createUnsortedLotList());
        assertAll(() -> assertSame(painting, sorted.get(0)), () -> assertSame(vase, sorted.get(1)), () -> assertSame(chair, sorted.get(2)));
    }

    @Test
    void sortBiddersByNameAscending() {
        LinkedList<Bidder> sorted = auctionSystem.sortBiddersByNameAscending(createUnsortedBidderList());
        assertAll(() -> assertSame(alice, sorted.get(0)), () -> assertSame(john, sorted.get(1)), () -> assertSame(mary, sorted.get(2)));
    }

    @Test
    void sortBiddersByAddressAscending() {
        LinkedList<Bidder> sorted = auctionSystem.sortBiddersByAddressAscending(createUnsortedBidderList());
        assertAll(() -> assertSame(alice, sorted.get(0)), () -> assertSame(mary, sorted.get(1)), () -> assertSame(john, sorted.get(2)));
    }

    @Test
    void getAuctionLots() {
        LinkedList<AuctionLot> lots = auctionSystem.getAuctionLots();
        assertAll(() -> assertNotNull(lots), () -> assertEquals(3, lots.size()), () -> assertTrue(lots.contains(chair)), () -> assertTrue(lots.contains(vase)), () -> assertTrue(lots.contains(painting)));
    }

    @Test
    void getBidders() {
        LinkedList<Bidder> result = auctionSystem.getBidders();
        assertAll(() -> assertNotNull(result), () -> assertEquals(3, result.size()), () -> assertTrue(result.contains(john)), () -> assertTrue(result.contains(alice)), () -> assertTrue(result.contains(mary)));
    }

    @Test
    void getBids() {
        Bid firstBid = auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        Bid secondBid = auctionSystem.placeBid(vase.getLotID(), mary.getBidderID(), 350);

        LinkedList<Bid> result = auctionSystem.getBids();
        assertAll(() -> assertNotNull(result), () -> assertEquals(2, result.size()), () -> assertTrue(result.contains(firstBid)), () -> assertTrue(result.contains(secondBid)));
    }

    @Test
    void getNumberOfAuctionLots() {
        assertEquals(3, auctionSystem.getNumberOfAuctionLots());
        auctionSystem.addAuctionLot("Antique Clock", "Victorian clock", "Clocks", 1890, 350);
        assertEquals(4, auctionSystem.getNumberOfAuctionLots());
    }

    @Test
    void getNumberOfBidders() {
        assertEquals(3, auctionSystem.getNumberOfBidders());
        auctionSystem.addBidder("David Jones", "Kilkenny City", "0831234567", "david@example.com");
        assertEquals(4, auctionSystem.getNumberOfBidders());
    }

    @Test
    void getNumberOfBids() {
        assertEquals(0, auctionSystem.getNumberOfBids());
        auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        auctionSystem.placeBid(vase.getLotID(), mary.getBidderID(), 350);
        assertEquals(2, auctionSystem.getNumberOfBids());
    }

    @Test
    void getNextLotID() {
        assertEquals(4, auctionSystem.getNextLotID());
        auctionSystem.addAuctionLot("Antique Clock", "Victorian clock", "Clocks", 1890, 350);
        assertEquals(5, auctionSystem.getNextLotID());
    }

    @Test
    void getNextBidderID() {
        assertEquals(4, auctionSystem.getNextBidderID());
        auctionSystem.addBidder("David Jones", "Kilkenny City", "0831234567", "david@example.com");
        assertEquals(5, auctionSystem.getNextBidderID());
    }

    @Test
    void getNextBidID() {
        assertEquals(1, auctionSystem.getNextBidID());
        auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        assertEquals(2, auctionSystem.getNextBidID());
    }

    @Test
    void clear() {
        auctionSystem.placeBid(chair.getLotID(), john.getBidderID(), 500);
        auctionSystem.clear();

        assertAll(() -> assertEquals(0, auctionSystem.getNumberOfAuctionLots()), () -> assertEquals(0, auctionSystem.getNumberOfBidders()), () -> assertEquals(0, auctionSystem.getNumberOfBids()), () -> assertEquals(1, auctionSystem.getNextLotID()), () -> assertEquals(1, auctionSystem.getNextBidderID()), () -> assertEquals(1, auctionSystem.getNextBidID()), () -> assertTrue(auctionSystem.getAuctionLots().isEmpty()), () -> assertTrue(auctionSystem.getBidders().isEmpty()), () -> assertTrue(auctionSystem.getBids().isEmpty()));
    }

    @Test
    void testToString() {
        String result = auctionSystem.toString();
        String expected = "Number of auction lots: 3" + "\nNumber of bidders: 3" + "\nNumber of bids: 0";
        assertEquals(expected, result);
    }

    private LinkedList<AuctionLot> createUnsortedLotList() {
        LinkedList<AuctionLot> lots = new LinkedList<AuctionLot>();

        lots.add(chair);
        lots.add(vase);
        lots.add(painting);
        return lots;
    }

    private LinkedList<Bidder> createUnsortedBidderList() {
        LinkedList<Bidder> result = new LinkedList<Bidder>();

        result.add(john);
        result.add(mary);
        result.add(alice);
        return result;
    }
}