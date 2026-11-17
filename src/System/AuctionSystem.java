package System;

public class AuctionSystem {
    private HashTable<AuctionLot> auctionLots;
    private HashTable<Bidder> bidders;
    private HashTable<Bid> bids;
    private int nextLotID;
    private int nextBidderID;
    private int nextBidID;

    // --------------------------------------------------
    // Constructor
    // --------------------------------------------------

    public AuctionSystem() {
        auctionLots = new HashTable<AuctionLot>();
        bidders = new HashTable<Bidder>();
        bids = new HashTable<Bid>();
        nextLotID = 1;
        nextBidderID = 1;
        nextBidID = 1;
    }

    // --------------------------------------------------
    // Auction lot management
    // --------------------------------------------------

    public AuctionLot addAuctionLot(String title, String description, String type, int estimatedYear, double guidePrice) {
        AuctionLot lot = new AuctionLot(nextLotID, title, description, type, estimatedYear, guidePrice);
        auctionLots.add(nextLotID, lot);
        nextLotID++;
        return lot;
    }

    public AuctionLot findAuctionLot(int lotID) {
        if (lotID < 1) {
            return null;
        }
        return auctionLots.get(lotID);
    }

    public boolean editAuctionLot(int lotID, String title, String description, String type, int estimatedYear, double guidePrice) {
        AuctionLot lot = findAuctionLot(lotID);

        if (lot == null) {
            return false;
        }
        lot.editDetails(title, description, type, estimatedYear, guidePrice);
        return true;
    }

    public boolean withdrawAuctionLot(int lotID) {
        AuctionLot lot = findAuctionLot(lotID);

        if (lot == null) {
            return false;
        }
        lot.withdrawLot();
        return true;
    }

    public boolean sellAuctionLot(int lotID) {
        AuctionLot lot = findAuctionLot(lotID);

        if (lot == null) {
            return false;
        }
        lot.sellLot();
        return true;
    }

    // --------------------------------------------------
    // Bidder management
    // --------------------------------------------------

    public Bidder addBidder(String name, String address, String telephone, String email) {
        Bidder bidder = new Bidder(nextBidderID, name, address, telephone, email);
        bidders.add(nextBidderID, bidder);
        nextBidderID++;
        return bidder;
    }

    public Bidder findBidder(int bidderID) {
        if (bidderID < 1) {
            return null;
        }
        return bidders.get(bidderID);
    }

    public boolean editBidder(int bidderID, String name, String address, String telephone, String email) {
        Bidder bidder = findBidder(bidderID);

        if (bidder == null) {
            return false;
        }
        bidder.editDetails(name, address, telephone, email);
        return true;
    }

    // --------------------------------------------------
    // Bid management
    // --------------------------------------------------

    public Bid placeBid(int lotID, int bidderID, double amount) {
        AuctionLot lot = findAuctionLot(lotID);

        if (lot == null) {
            throw new IllegalArgumentException("Auction lot does not exist.");
        }

        Bidder bidder = findBidder(bidderID);

        if (bidder == null) {
            throw new IllegalArgumentException("Bidder does not exist.");
        }

        if (!lot.isAvailable()) {
            throw new IllegalStateException("The auction lot is not available.");
        }

        Bid highestBid = lot.getHighestBid();

        if (highestBid != null && amount <= highestBid.getAmount()) {
            throw new IllegalArgumentException("The bid must be higher than €" + highestBid.getAmount());
        }

        Bid bid = new Bid(nextBidID, bidder, lot, amount);
        lot.addBid(bid);
        bidder.addBid(bid);
        bids.add(nextBidID, bid);
        nextBidID++;
        return bid;
    }

    public Bid findBid(int bidID) {
        if (bidID < 1) {
            return null;
        }
        return bids.get(bidID);
    }

    public boolean withdrawBid(int bidID) {
        Bid bid = findBid(bidID);

        if (bid == null || bid.isWithdrawn()) {
            return false;
        }
        bid.withdrawBid();
        return true;
    }

    // --------------------------------------------------
    // Auction lot searching
    // --------------------------------------------------

    public LinkedList<AuctionLot> searchAuctionLots(String partialTitle, String type, Integer estimatedYear, String descriptionKeyword) {
        LinkedList<AuctionLot> results = new LinkedList<AuctionLot>();
        LinkedList<AuctionLot> allLots = auctionLots.values();

        for (int i = 0; i < allLots.size(); i++) {
            AuctionLot lot = allLots.get(i);

            if (lot.matchesSearch(partialTitle, type, estimatedYear, descriptionKeyword)) {
                results.add(lot);
            }
        }
        return results;
    }

    // --------------------------------------------------
    // Bidder searching
    // --------------------------------------------------

    public LinkedList<Bidder> searchBidders(String partialName, String partialAddress) {
        LinkedList<Bidder> results = new LinkedList<Bidder>();
        LinkedList<Bidder> allBidders = bidders.values();

        for (int i = 0; i < allBidders.size(); i++) {
            Bidder bidder = allBidders.get(i);

            boolean nameMatches = partialName == null || partialName.isBlank() || bidder.getName().toLowerCase().contains(partialName.trim().toLowerCase());

            boolean addressMatches = partialAddress == null || partialAddress.isBlank() || bidder.getAddress().toLowerCase().contains(partialAddress.trim().toLowerCase());

            if (nameMatches && addressMatches) {
                results.add(bidder);
            }
        }
        return results;
    }

    // --------------------------------------------------
    // Auction lot sorting
    // --------------------------------------------------

    public LinkedList<AuctionLot> sortLotsByTitleAscending(LinkedList<AuctionLot> source) {
        validateLotList(source);
        LinkedList<AuctionLot> remaining = copyLots(source);
        LinkedList<AuctionLot> sorted = new LinkedList<AuctionLot>();

        while (!remaining.isEmpty()) {
            int smallestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentTitle = remaining.get(i).getTitle();
                String smallestTitle = remaining.get(smallestIndex).getTitle();

                if (currentTitle.compareToIgnoreCase(smallestTitle) < 0) {
                    smallestIndex = i;
                }
            }
            sorted.add(remaining.remove(smallestIndex));
        }
        return sorted;
    }

    public LinkedList<AuctionLot> sortLotsByYearAscending(LinkedList<AuctionLot> source) {validateLotList(source);
        LinkedList<AuctionLot> remaining = copyLots(source);
        LinkedList<AuctionLot> sorted = new LinkedList<AuctionLot>();

        while (!remaining.isEmpty()) {
            int earliestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                if (remaining.get(i).getEstimatedYear() < remaining.get(earliestIndex).getEstimatedYear()) {
                    earliestIndex = i;
                }
            }
            sorted.add(remaining.remove(earliestIndex));
        }
        return sorted;
    }

    public LinkedList<AuctionLot> sortLotsByYearDescending(LinkedList<AuctionLot> source) {
        validateLotList(source);
        LinkedList<AuctionLot> remaining = copyLots(source);
        LinkedList<AuctionLot> sorted = new LinkedList<AuctionLot>();

        while (!remaining.isEmpty()) {
            int newestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                if (remaining.get(i).getEstimatedYear() > remaining.get(newestIndex).getEstimatedYear()) {
                    newestIndex = i;
                }
            }
            sorted.add(remaining.remove(newestIndex));
        }
        return sorted;
    }

    public LinkedList<AuctionLot> sortLotsByTypeAscending(LinkedList<AuctionLot> source) {
        validateLotList(source);
        LinkedList<AuctionLot> remaining = copyLots(source);
        LinkedList<AuctionLot> sorted = new LinkedList<AuctionLot>();

        while (!remaining.isEmpty()) {
            int smallestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentType = remaining.get(i).getType();
                String smallestType = remaining.get(smallestIndex).getType();

                if (currentType.compareToIgnoreCase(smallestType) < 0) {
                    smallestIndex = i;
                }
            }
            sorted.add(remaining.remove(smallestIndex));
        }
        return sorted;
    }

    // --------------------------------------------------
    // Bidder sorting
    // --------------------------------------------------

    public LinkedList<Bidder> sortBiddersByNameAscending(LinkedList<Bidder> source) {
        validateBidderList(source);
        LinkedList<Bidder> remaining = copyBidders(source);
        LinkedList<Bidder> sorted = new LinkedList<Bidder>();

        while (!remaining.isEmpty()) {
            int smallestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentName = remaining.get(i).getName();
                String smallestName = remaining.get(smallestIndex).getName();

                if (currentName.compareToIgnoreCase(smallestName) < 0) {
                    smallestIndex = i;
                }
            }
            sorted.add(remaining.remove(smallestIndex));
        }
        return sorted;
    }

    public LinkedList<Bidder> sortBiddersByAddressAscending(LinkedList<Bidder> source) {
        validateBidderList(source);
        LinkedList<Bidder> remaining = copyBidders(source);
        LinkedList<Bidder> sorted = new LinkedList<Bidder>();

        while (!remaining.isEmpty()) {
            int smallestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                String currentAddress = remaining.get(i).getAddress();
                String smallestAddress = remaining.get(smallestIndex).getAddress();

                if (currentAddress.compareToIgnoreCase(smallestAddress) < 0) {
                    smallestIndex = i;
                }
            }
            sorted.add(remaining.remove(smallestIndex));
        }
        return sorted;
    }

    private void validateLotList(LinkedList<AuctionLot> source) {
        if (source == null) {
            throw new IllegalArgumentException("Auction lot list cannot be null.");
        }
    }

    private void validateBidderList(LinkedList<Bidder> source) {
        if (source == null) {
            throw new IllegalArgumentException("Bidder list cannot be null.");
        }
    }

    private LinkedList<AuctionLot> copyLots(LinkedList<AuctionLot> source) {
        LinkedList<AuctionLot> copy = new LinkedList<AuctionLot>();

        for (int i = 0; i < source.size(); i++) {
            copy.add(source.get(i));
        }
        return copy;
    }

    private LinkedList<Bidder> copyBidders(LinkedList<Bidder> source) {
        LinkedList<Bidder> copy = new LinkedList<Bidder>();

        for (int i = 0; i < source.size(); i++) {
            copy.add(source.get(i));
        }
        return copy;
    }

    // --------------------------------------------------
    // General information
    // --------------------------------------------------

    public LinkedList<AuctionLot> getAuctionLots() {
        return auctionLots.values();
    }

    public LinkedList<Bidder> getBidders() {
        return bidders.values();
    }

    public LinkedList<Bid> getBids() {
        return bids.values();
    }

    public int getNumberOfAuctionLots() {
        return auctionLots.size();
    }

    public int getNumberOfBidders() {
        return bidders.size();
    }

    public int getNumberOfBids() {
        return bids.size();
    }

    public int getNextLotID() {
        return nextLotID;
    }

    public int getNextBidderID() {
        return nextBidderID;
    }

    public int getNextBidID() {
        return nextBidID;
    }

    public void clear() {
        auctionLots.clear();
        bidders.clear();
        bids.clear();
        nextLotID = 1;
        nextBidderID = 1;
        nextBidID = 1;
    }

    // --------------------------------------------------
    // toString
    // --------------------------------------------------

    @Override
    public String toString() {
        return "Number of auction lots: " + getNumberOfAuctionLots() + "\nNumber of bidders: " + getNumberOfBidders() + "\nNumber of bids: " + getNumberOfBids();
    }
}