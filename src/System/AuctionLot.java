package System;

import java.time.LocalDateTime;

public class AuctionLot {
    private final int lotID;
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

    // --------------------------------------------------
    // Constructor
    // --------------------------------------------------

    public AuctionLot(int lotID, String title, String description, String type, int estimatedYear, double guidePrice) {
        if (lotID < 1 || lotID > 999999) {
            throw new IllegalArgumentException("Lot ID must be between 1 and 999999.");
        }

        this.lotID = lotID;
        bids = new LinkedList<Bid>();

        validateTitle(title);
        validateDescription(description);
        validateType(type);
        validateEstimatedYear(estimatedYear);
        validateGuidePrice(guidePrice);

        this.title = title.trim();
        this.description = description.trim();
        this.type = type.trim();
        this.estimatedYear = estimatedYear;
        this.guidePrice = guidePrice;

        sold = false;
        withdrawn = false;
        saleDateTime = null;
        finalPrice = 0;
    }

    // --------------------------------------------------
    // Lot ID
    // --------------------------------------------------

    public int getLotID() {
        return lotID;
    }

    // --------------------------------------------------
    // Title
    // --------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        ensureEditable();
        validateTitle(title);
        this.title = title.trim();
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Auction lot title cannot be empty.");
        }

        if (title.trim().length() > 100) {
            throw new IllegalArgumentException("Auction lot title cannot exceed 100 characters.");
        }
    }

    // --------------------------------------------------
    // Description
    // --------------------------------------------------

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        ensureEditable();
        validateDescription(description);
        this.description = description.trim();
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Auction lot description cannot be empty.");
        }

        if (description.trim().length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters.");
        }
    }

    // --------------------------------------------------
    // Type
    // --------------------------------------------------

    public String getType() {
        return type;
    }

    public void setType(String type) {
        ensureEditable();
        validateType(type);
        this.type = type.trim();
    }

    private void validateType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Auction lot type cannot be empty.");
        }

        if (type.trim().length() > 50) {
            throw new IllegalArgumentException("Auction lot type cannot exceed 50 characters.");
        }
    }

    // --------------------------------------------------
    // Estimated year
    // --------------------------------------------------

    public int getEstimatedYear() {
        return estimatedYear;
    }

    public void setEstimatedYear(int estimatedYear) {
        ensureEditable();
        validateEstimatedYear(estimatedYear);
        this.estimatedYear = estimatedYear;
    }

    private void validateEstimatedYear(int estimatedYear) {
        int currentYear = java.time.Year.now().getValue();

        if (estimatedYear < 1 || estimatedYear > currentYear) {
            throw new IllegalArgumentException("Estimated year must be between 1 and " + currentYear);
        }
    }

    // --------------------------------------------------
    // Guide price
    // --------------------------------------------------

    public double getGuidePrice() {
        return guidePrice;
    }

    public void setGuidePrice(double guidePrice) {
        ensureEditable();
        validateGuidePrice(guidePrice);
        this.guidePrice = guidePrice;
    }

    private void validateGuidePrice(double guidePrice) {
        if (Double.isNaN(guidePrice) || Double.isInfinite(guidePrice) || guidePrice < 0) {
            throw new IllegalArgumentException("Guide price cannot be negative.");
        }
    }

    // --------------------------------------------------
    // Complete editing
    // --------------------------------------------------

    public void editDetails(String newTitle, String newDescription, String newType, int newEstimatedYear, double newGuidePrice) {
        ensureEditable();
        validateTitle(newTitle);
        validateDescription(newDescription);
        validateType(newType);
        validateEstimatedYear(newEstimatedYear);
        validateGuidePrice(newGuidePrice);

        title = newTitle.trim();
        description = newDescription.trim();
        type = newType.trim();
        estimatedYear = newEstimatedYear;
        guidePrice = newGuidePrice;
    }

    private void ensureEditable() {
        if (sold) {
            throw new IllegalStateException("A sold auction lot cannot be edited.");
        }

        if (withdrawn) {
            throw new IllegalStateException("A withdrawn auction lot cannot be edited.");
        }
    }

    // --------------------------------------------------
    // Lot status
    // --------------------------------------------------

    public boolean isSold() {
        return sold;
    }

    public boolean isWithdrawn() {
        return withdrawn;
    }

    public boolean isAvailable() {
        return !sold && !withdrawn;
    }

    public LocalDateTime getSaleDateTime() {
        return saleDateTime;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public String getStatusDescription() {
        if (sold) {
            return "SOLD";
        }

        if (withdrawn) {
            return "WITHDRAWN";
        }
        return "AVAILABLE";
    }

    public void withdrawLot() {
        if (sold) {
            throw new IllegalStateException("A sold auction lot cannot be withdrawn.");
        }

        if (withdrawn) {
            throw new IllegalStateException("The lot has already been withdrawn.");
        }

        withdrawn = true;
    }

    // --------------------------------------------------
    // Bid management
    // --------------------------------------------------

    public LinkedList<Bid> getBids() {
        return bids;
    }

    public int getNumberOfBids() {
        return bids.size();
    }

    public boolean addBid(Bid bid) {
        if (bid == null) {
            throw new IllegalArgumentException("Bid cannot be null.");
        }

        if (!isAvailable()) {
            throw new IllegalStateException("Bids cannot be added to a sold or withdrawn auction lot.");
        }

        if (bid.getAuctionLot() != this) {
            throw new IllegalArgumentException("The bid belongs to a different auction lot.");
        }

        if (findBid(bid.getBidID()) != null) {
            return false;
        }

        Bid currentHighestBid = getHighestBid();

        if (currentHighestBid != null && bid.getAmount() <= currentHighestBid.getAmount()) {
            throw new IllegalArgumentException("The new bid must be higher than €" + currentHighestBid.getAmount());
        }

        bids.add(bid);
        return true;
    }

    public Bid findBid(int bidID) {
        if (bidID < 1) {
            return null;
        }

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);

            if (bid.getBidID() == bidID) {
                return bid;
            }
        }
        return null;
    }

    public Bid getHighestBid() {
        Bid highestBid = null;

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);

            if (!bid.isWithdrawn() && bid.isHigherThan(highestBid)) {
                highestBid = bid;
            }
        }
        return highestBid;
    }

    public int getNumberOfActiveBids() {
        int count = 0;

        for (int i = 0; i < bids.size(); i++) {
            if (bids.get(i).isActive()) {
                count++;
            }
        }
        return count;
    }

    // --------------------------------------------------
    // Selling
    // --------------------------------------------------

    public void sellLot() {
        if (sold) {
            throw new IllegalStateException("The auction lot has already been sold.");
        }

        if (withdrawn) {
            throw new IllegalStateException("A withdrawn auction lot cannot be sold.");
        }

        Bid highestBid = getHighestBid();

        if (highestBid == null) {
            throw new IllegalStateException("The auction lot has no active bids.");
        }

        highestBid.markAsWinningBid();

        sold = true;
        finalPrice = highestBid.getAmount();
        saleDateTime = LocalDateTime.now();
    }

    // --------------------------------------------------
    // Bid sorting
    // --------------------------------------------------

    public LinkedList<Bid> getBidsSortedByAmountDescending() {
        LinkedList<Bid> remaining = copyBids();
        LinkedList<Bid> sorted = new LinkedList<Bid>();

        while (!remaining.isEmpty()) {
            int highestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                Bid currentBid = remaining.get(i);

                Bid highestBid = remaining.get(highestIndex);

                if (currentBid.getAmount() > highestBid.getAmount()) {
                    highestIndex = i;
                }
            }
            sorted.add(remaining.remove(highestIndex));
        }
        return sorted;
    }

    private LinkedList<Bid> copyBids() {
        LinkedList<Bid> copy = new LinkedList<Bid>();

        for (int i = 0; i < bids.size(); i++) {
            copy.add(bids.get(i));
        }
        return copy;
    }

    // --------------------------------------------------
    // Search matching
    // --------------------------------------------------

    public boolean matchesSearch(String partialTitle, String requestedType, Integer requestedYear, String descriptionKeyword) {
        boolean titleMatches = partialTitle == null || partialTitle.isBlank() || title.toLowerCase().contains(partialTitle.trim().toLowerCase());

        boolean typeMatches = requestedType == null || requestedType.isBlank() || type.equalsIgnoreCase(requestedType.trim());

        boolean yearMatches = requestedYear == null || estimatedYear == requestedYear;

        boolean descriptionMatches = descriptionKeyword == null || descriptionKeyword.isBlank() || description.toLowerCase().contains(descriptionKeyword.trim().toLowerCase());

        return titleMatches && typeMatches && yearMatches && descriptionMatches;
    }

    // --------------------------------------------------
    // Detailed report
    // --------------------------------------------------

    public String getDetailedReport() {
        String report = toString();
        LinkedList<Bid> sortedBids = getBidsSortedByAmountDescending();

        if (sortedBids.isEmpty()) {
            return report + "\n\nNo bids recorded.";
        }

        report += "\n\nBIDS - HIGHEST FIRST";

        for (int i = 0; i < sortedBids.size(); i++) {
            Bid bid = sortedBids.get(i);

            report += "\n\n-------------------------";

            if (bid.isWinningBid()) {
                report += "\nWINNING BID";
            }

            if (bid.isWithdrawn()) {
                report += "\nWITHDRAWN BID";
            }

            report += "\n" + bid;
        }
        return report;
    }

    // --------------------------------------------------
    // toString
    // --------------------------------------------------

    @Override
    public String toString() {
        String result = "Auction lot ID: " + lotID + "\nTitle: " + title + "\nDescription: " + description + "\nType: " + type + "\nEstimated year: " + estimatedYear + "\nGuide price: €" + guidePrice + "\nStatus: " + getStatusDescription() + "\nNumber of bids: " + getNumberOfBids();

        if (sold) {
            result += "\nFinal sale price: €" + finalPrice + "\nSale date/time: " + saleDateTime;
        }
        return result;
    }
}