package System;

public class Bidder {
    private final int bidderID;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private LinkedList<Bid> bids;

    // --------------------------------------------------
    // Constructor
    // --------------------------------------------------

    public Bidder(int bidderID, String name, String address, String telephone, String email) {
        if (bidderID < 1 || bidderID > 999999) {
            throw new IllegalArgumentException("Bidder ID must be between 1 and 999999.");
        }

        this.bidderID = bidderID;
        bids = new LinkedList<Bid>();
        setName(name);
        setAddress(address);
        setTelephone(telephone);
        setEmail(email);
    }

    // --------------------------------------------------
    // Bidder ID
    // --------------------------------------------------

    public int getBidderID() {
        return bidderID;
    }

    // --------------------------------------------------
    // Name
    // --------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bidder name cannot be empty.");
        }
        this.name = name;
    }

    // --------------------------------------------------
    // Address
    // --------------------------------------------------

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Bidder address cannot be empty.");
        }
        this.address = address;
    }

    // --------------------------------------------------
    // Telephone
    // --------------------------------------------------

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        validateTelephone(telephone);
        this.telephone = telephone;
    }

    private void validateTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw new IllegalArgumentException("Telephone number cannot be empty.");
        }
    }

    // --------------------------------------------------
    // Email
    // --------------------------------------------------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be empty.");
        }
    }

    // --------------------------------------------------
    // Edit details
    // --------------------------------------------------

    public void editDetails(String newName, String newAddress, String newTelephone, String newEmail) {
        validateName(newName);
        validateAddress(newAddress);
        validateTelephone(newTelephone);
        validateEmail(newEmail);

        name = newName;
        address = newAddress;
        telephone = newTelephone;
        email = newEmail;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Bidder name cannot be empty.");
        }

        if (name.trim().length() > 60) {
            throw new IllegalArgumentException("Bidder name cannot exceed 60 characters.");
        }
    }

    private void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Bidder address cannot be empty.");
        }

        if (address.trim().length() > 150) {
            throw new IllegalArgumentException("Bidder address cannot exceed 150 characters.");
        }
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

        if (bid.getBidder() != this) {
            throw new IllegalArgumentException("The bid belongs to a different bidder.");
        }

        if (findBid(bid.getBidID()) != null) {
            return false;
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

    public boolean withdrawBid(int bidID) {
        Bid bid = findBid(bidID);

        if (bid == null || bid.isWithdrawn()) {
            return false;
        }
        bid.withdrawBid();
        return true;
    }

    public int getNumberOfActiveBids() {
        int count = 0;

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);

            if (bid.isActive()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfWithdrawnBids() {
        int count = 0;

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);

            if (bid.isWithdrawn()) {
                count++;
            }
        }
        return count;
    }

    public int getNumberOfWinningBids() {
        int count = 0;

        for (int i = 0; i < bids.size(); i++) {
            Bid bid = bids.get(i);

            if (bid.isWinningBid()) {
                count++;
            }
        }
        return count;
    }

    public boolean hasWinningBid() {
        return getNumberOfWinningBids() > 0;
    }

    // --------------------------------------------------
    // Bid sorting
    // --------------------------------------------------

    public LinkedList<Bid> getBidsSortedByDateAscending() {

        LinkedList<Bid> remaining = copyBids();
        LinkedList<Bid> sorted = new LinkedList<Bid>();

        while (!remaining.isEmpty()) {
            int earliestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                Bid currentBid = remaining.get(i);
                Bid earliestBid = remaining.get(earliestIndex);

                if (currentBid.getDateTime().isBefore(earliestBid.getDateTime())) {
                    earliestIndex = i;
                }
            }

            Bid earliestBid = remaining.remove(earliestIndex);
            sorted.add(earliestBid);
        }
        return sorted;
    }

    public LinkedList<Bid> getBidsSortedByDateDescending() {
        LinkedList<Bid> remaining = copyBids();
        LinkedList<Bid> sorted = new LinkedList<Bid>();

        while (!remaining.isEmpty()) {
            int newestIndex = 0;

            for (int i = 1; i < remaining.size(); i++) {
                Bid currentBid = remaining.get(i);
                Bid newestBid = remaining.get(newestIndex);

                if (currentBid.getDateTime().isAfter(newestBid.getDateTime())) {
                    newestIndex = i;
                }
            }

            Bid newestBid = remaining.remove(newestIndex);
            sorted.add(newestBid);
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
    // Search support
    // --------------------------------------------------

    public boolean nameContains(String partialName) {

        if (partialName == null || partialName.isBlank()) {
            return true;
        }
        return name.toLowerCase().contains(partialName.trim().toLowerCase());
    }

    public boolean addressContains(String partialAddress) {
        if (partialAddress == null || partialAddress.isBlank()) {
            return true;
        }

        return address.toLowerCase().contains(partialAddress.trim().toLowerCase());
    }

    public boolean matchesSearch(String partialName, String partialAddress) {
        return nameContains(partialName) && addressContains(partialAddress);
    }

    // --------------------------------------------------
    // Detailed report
    // --------------------------------------------------

    public String getDetailedReport() {
        String report = toString() + "\nNumber of bids: " + getNumberOfBids() + "\nActive bids: " + getNumberOfActiveBids() + "\nWithdrawn bids: " + getNumberOfWithdrawnBids() + "\nWinning bids: " + getNumberOfWinningBids();

        LinkedList<Bid> sortedBids = getBidsSortedByDateAscending();

        if (sortedBids.isEmpty()) {
            return report + "\nNo bids recorded.";
        }

        report += "\n\nBID HISTORY";

        for (int i = 0; i < sortedBids.size(); i++) {
            Bid bid = sortedBids.get(i);

            report += "\n\n-------------------------";

            if (bid.isWinningBid()) {
                report += "\n*** WINNING BID ***";
            }

            if (bid.isWithdrawn()) {
                report += "\n*** WITHDRAWN BID ***";
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
        return "Bidder ID: " + bidderID + "\nName: " + name + "\nAddress: " + address + "\nTelephone: " + telephone + "\nEmail: " + email;
    }
}