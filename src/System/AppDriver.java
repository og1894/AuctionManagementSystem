package System;

import java.io.IOException;
import java.util.Scanner;

public class AppDriver {
    private final Scanner input;
    private final XStreamPersistence persistence;
    private AuctionSystem auctionSystem;

    // --------------------------------------------------
    // Constructor and main method
    // --------------------------------------------------

    public AppDriver() {
        input = new Scanner(System.in);
        persistence = new XStreamPersistence();
        auctionSystem = new AuctionSystem();
    }

    public static void main(String[] args) {
        AppDriver driver = new AppDriver();
        driver.run();
    }

    // --------------------------------------------------
    // Main program loop
    // --------------------------------------------------

    public void run() {
        boolean running = true;
        System.out.println("AUCTION MANAGEMENT SYSTEM");
        loadAutomatically();

        while (running) {
            displayMainMenu();

            int option = readInteger("Select an option: ");

            try {
                switch (option) {
                    case 1 -> addAuctionLot();
                    case 2 -> addBidder();
                    case 3 -> placeBid();
                    case 4 -> sellAuctionLot();
                    case 5 -> withdrawAuctionLot();
                    case 6 -> withdrawBid();
                    case 7 -> editAuctionLot();
                    case 8 -> editBidder();
                    case 9 -> searchAuctionLots();
                    case 10 -> searchBidders();
                    case 11 -> viewAuctionLotDetails();
                    case 12 -> viewBidderDetails();
                    case 13 -> displayAllAuctionLots();
                    case 14 -> displayAllBidders();
                    case 15 -> displayAllBids();
                    case 16 -> displaySystemSummary();
                    case 17 -> saveSystem();
                    case 18 -> loadSystem();
                    case 19 -> resetSystem();
                    case 0 -> {saveAutomatically();running = false;
                    }
                    default -> System.out.println("Invalid menu option.");
                }
            }

            catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
        input.close();
        System.out.println("Auction management system closed.");
    }

    private void displayMainMenu() {
        System.out.println("\n---- MAIN MENU ----");
        System.out.println("1. Add auction lot");
        System.out.println("2. Add bidder");
        System.out.println("3. Place bid");
        System.out.println("4. Sell auction lot");
        System.out.println("5. Withdraw auction lot");
        System.out.println("6. Withdraw bid");
        System.out.println("7. Edit auction lot");
        System.out.println("8. Edit bidder");
        System.out.println("9. Search auction lots");
        System.out.println("10. Search bidders");
        System.out.println("11. View auction lot details");
        System.out.println("12. View bidder details");
        System.out.println("13. Display all auction lots");
        System.out.println("14. Display all bidders");
        System.out.println("15. Display all bids");
        System.out.println("16. Display system summary");
        System.out.println("17. Save system");
        System.out.println("18. Load system");
        System.out.println("19. Reset system");
        System.out.println("0. Save and exit");
    }

    // --------------------------------------------------
    // Add auction lot
    // --------------------------------------------------

    private void addAuctionLot() {
        System.out.println("\nADD AUCTION LOT");

        String title = readRequiredString("Title: ");
        String description = readRequiredString("Description: ");
        String type = readRequiredString("Type: ");
        int estimatedYear = readPositiveInteger("Estimated year of origin: ");
        double guidePrice = readNonNegativeDouble("Guide price: €");

        AuctionLot lot = auctionSystem.addAuctionLot(title, description, type, estimatedYear, guidePrice);
        System.out.println("Auction lot added successfully.");
        System.out.println("Auction lot ID: " + lot.getLotID());
    }

    // --------------------------------------------------
    // Add bidder
    // --------------------------------------------------

    private void addBidder() {
        System.out.println("\nADD BIDDER");

        String name = readRequiredString("Name: ");
        String address = readRequiredString("Address: ");
        String telephone = readRequiredString("Telephone: ");
        String email = readRequiredString("Email: ");
        Bidder bidder = auctionSystem.addBidder(name, address, telephone, email);

        System.out.println("Bidder added successfully.");
        System.out.println("Bidder ID: " + bidder.getBidderID());
    }

    // --------------------------------------------------
    // Place bid
    // --------------------------------------------------

    private void placeBid() {
        System.out.println("\nPLACE BID");

        int lotID = readPositiveInteger("Auction lot ID: ");

        AuctionLot lot = auctionSystem.findAuctionLot(lotID);

        if (lot == null) {
            System.out.println("Auction lot not found.");
            return;
        }

        if (!lot.isAvailable()) {
            System.out.println("This auction lot is " + lot.getStatusDescription());
            return;
        }

        Bid highestBid = lot.getHighestBid();

        if (highestBid == null) {
            System.out.println("No bids have been placed.");
        }

        else {
            System.out.println("Current highest bid: €" + highestBid.getAmount());
        }

        int bidderID = readPositiveInteger("Bidder ID: ");

        if (auctionSystem.findBidder(bidderID) == null) {
            System.out.println("Bidder not found.");
            return;
        }

        double amount = readPositiveDouble("New bid amount: €");

        Bid bid = auctionSystem.placeBid(lotID, bidderID, amount);
        System.out.println("Bid added successfully.");
        System.out.println(bid);
    }

    // --------------------------------------------------
    // Sell auction lot
    // --------------------------------------------------

    private void sellAuctionLot() {
        System.out.println("\nSELL AUCTION LOT");

        int lotID = readPositiveInteger("Auction lot ID: ");

        AuctionLot lot = auctionSystem.findAuctionLot(lotID);

        if (lot == null) {
            System.out.println("Auction lot not found.");
            return;
        }

        Bid highestBid = lot.getHighestBid();

        if (highestBid == null) {
            System.out.println("The auction lot has no active bids.");
            return;
        }

        System.out.println("Highest bid: €" + highestBid.getAmount());
        System.out.println("Bidder: " + highestBid.getBidder().getName());

        boolean confirmSale = readBoolean("Accept this bid and sell the lot? (yes/no): ");

        if (!confirmSale) {
            System.out.println("Sale cancelled.");
            return;
        }

        auctionSystem.sellAuctionLot(lotID);
        System.out.println("Auction lot sold successfully.");
        System.out.println("Final sale price: €" + lot.getFinalPrice());
        System.out.println("Sale date/time: " + lot.getSaleDateTime());
    }

    // --------------------------------------------------
    // Withdraw auction lot
    // --------------------------------------------------

    private void withdrawAuctionLot() {
        System.out.println("\nWITHDRAW AUCTION LOT");

        int lotID = readPositiveInteger("Auction lot ID: ");

        AuctionLot lot = auctionSystem.findAuctionLot(lotID);

        if (lot == null) {
            System.out.println("Auction lot not found.");
            return;
        }

        boolean confirmed = readBoolean("Withdraw " + lot.getTitle() + "? (yes/no): ");

        if (!confirmed) {
            System.out.println("Withdrawal cancelled.");
            return;
        }

        auctionSystem.withdrawAuctionLot(lotID);
        System.out.println("Auction lot withdrawn successfully.");
    }

    // --------------------------------------------------
    // Withdraw bid
    // --------------------------------------------------

    private void withdrawBid() {
        System.out.println("\nWITHDRAW BID");

        int bidID = readPositiveInteger("Bid ID: ");

        Bid bid = auctionSystem.findBid(bidID);

        if (bid == null) {
            System.out.println("Bid not found.");
            return;
        }

        System.out.println(bid);

        boolean confirmed = readBoolean("Withdraw this bid? (yes/no): ");

        if (!confirmed) {
            System.out.println("Withdrawal cancelled.");
            return;
        }

        boolean withdrawn = auctionSystem.withdrawBid(bidID);

        if (withdrawn) {
            System.out.println("Bid withdrawn successfully.");
        }

        else {
            System.out.println("The bid could not be withdrawn.");
        }
    }

    // --------------------------------------------------
    // Edit auction lot
    // --------------------------------------------------

    private void editAuctionLot() {
        System.out.println("\nEDIT AUCTION LOT");

        int lotID = readPositiveInteger("Auction lot ID: ");

        AuctionLot lot = auctionSystem.findAuctionLot(lotID);

        if (lot == null) {
            System.out.println("Auction lot not found.");
            return;
        }

        System.out.println("\nCURRENT DETAILS");
        System.out.println(lot);

        String title = readRequiredString("New title: ");
        String description = readRequiredString("New description: ");
        String type = readRequiredString("New type: ");
        int estimatedYear = readPositiveInteger("New estimated year: ");
        double guidePrice = readNonNegativeDouble("New guide price: €");

        auctionSystem.editAuctionLot(lotID, title, description, type, estimatedYear, guidePrice);
        System.out.println("Auction lot updated successfully.");
    }

    // --------------------------------------------------
    // Edit bidder
    // --------------------------------------------------

    private void editBidder() {
        System.out.println("\nEDIT BIDDER");

        int bidderID = readPositiveInteger("Bidder ID: ");

        Bidder bidder = auctionSystem.findBidder(bidderID);

        if (bidder == null) {
            System.out.println("Bidder not found.");
            return;
        }

        System.out.println("\nCURRENT DETAILS");
        System.out.println(bidder);

        String name = readRequiredString("New name: ");
        String address = readRequiredString("New address: ");
        String telephone = readRequiredString("New telephone: ");
        String email = readRequiredString("New email: ");

        auctionSystem.editBidder(bidderID, name, address, telephone, email);
        System.out.println("Bidder updated successfully.");
    }

    // --------------------------------------------------
    // Search auction lots
    // --------------------------------------------------

    private void searchAuctionLots() {
        System.out.println("\nSEARCH AUCTION LOTS");

        String title = readString("Partial title (Enter for any): ");
        String type = readString("Type (Enter for any): ");
        Integer year = readOptionalInteger("Estimated year (Enter for any): ");
        String description = readString("Description keyword (Enter for any): ");

        LinkedList<AuctionLot> results = auctionSystem.searchAuctionLots(title, type, year, description);

        if (results.isEmpty()) {
            System.out.println("No matching auction lots.");
            return;
        }

        System.out.println("\nSORT RESULTS");
        System.out.println("1. Title ascending");
        System.out.println("2. Year ascending");
        System.out.println("3. Year descending");
        System.out.println("4. Type ascending");
        System.out.println("0. Do not sort");

        int sortOption = readInteger("Sort option: ");

        switch (sortOption) {
            case 1 -> results = auctionSystem.sortLotsByTitleAscending(results);
            case 2 -> results = auctionSystem.sortLotsByYearAscending(results);
            case 3 -> results = auctionSystem.sortLotsByYearDescending(results);
            case 4 -> results = auctionSystem.sortLotsByTypeAscending(results);
            case 0 -> {}
            default -> System.out.println("Invalid sort option. Current order retained.");
        }

        displayAuctionLotResults(results);

        if (readBoolean("View full details for a lot? (yes/no): ")) {
            viewAuctionLotDetails();
        }
    }

    // --------------------------------------------------
    // Search bidders
    // --------------------------------------------------

    private void searchBidders() {
        System.out.println("\nSEARCH BIDDERS");

        String name = readString("Partial name (Enter for any): ");
        String address = readString("Partial address (Enter for any): ");

        LinkedList<Bidder> results = auctionSystem.searchBidders(name, address);

        if (results.isEmpty()) {System.out.println("No matching bidders.");
            return;
        }

        System.out.println("\nSORT RESULTS");
        System.out.println("1. Name ascending");
        System.out.println("2. Address ascending");
        System.out.println("0. Do not sort");

        int sortOption = readInteger("Sort option: ");

        if (sortOption == 1) {
            results = auctionSystem.sortBiddersByNameAscending(results);
        }

        else if (sortOption == 2) {
            results = auctionSystem.sortBiddersByAddressAscending(results);
        }

        displayBidderResults(results);

        if (readBoolean("View full details for a bidder? (yes/no): ")) {
            viewBidderDetails();
        }
    }

    // --------------------------------------------------
    // Auction lot detail and drill-down
    // --------------------------------------------------

    private void viewAuctionLotDetails() {
        System.out.println("\nVIEW AUCTION LOT DETAILS");

        int lotID = readPositiveInteger("Auction lot ID: ");

        AuctionLot lot = auctionSystem.findAuctionLot(lotID);

        if (lot == null) {System.out.println("Auction lot not found.");
            return;
        }

        System.out.println("\n" + lot.getDetailedReport());

        if (lot.getBids().isEmpty()) {
            return;
        }

        boolean openBidder = readBoolean("Open bidder details from a bid? (yes/no): ");

        if (!openBidder) {
            return;
        }

        int bidID = readPositiveInteger("Bid ID: ");

        Bid bid = lot.findBid(bidID);

        if (bid == null) {
            System.out.println("That bid does not belong to this auction lot.");
            return;
        }

        System.out.println("\n" + bid.getBidder().getDetailedReport());
    }

    // --------------------------------------------------
    // Bidder detail and drill-down
    // --------------------------------------------------

    private void viewBidderDetails() {
        System.out.println("\nVIEW BIDDER DETAILS");

        int bidderID = readPositiveInteger("Bidder ID: ");

        Bidder bidder = auctionSystem.findBidder(bidderID);

        if (bidder == null) {
            System.out.println("Bidder not found.");
            return;
        }

        System.out.println("\n" + bidder.getDetailedReport());

        if (bidder.getBids().isEmpty()) {
            return;
        }

        boolean openLot = readBoolean("Open auction lot details from a bid? (yes/no): ");

        if (!openLot) {
            return;
        }

        int bidID = readPositiveInteger("Bid ID: ");

        Bid bid = bidder.findBid(bidID);

        if (bid == null) {
            System.out.println("That bid does not belong to this bidder.");
            return;
        }

        System.out.println("\n" + bid.getAuctionLot().getDetailedReport());
    }

    // --------------------------------------------------
    // General listings
    // --------------------------------------------------

    private void displayAllAuctionLots() {
        LinkedList<AuctionLot> lots = auctionSystem.getAuctionLots();

        if (lots.isEmpty()) {
            System.out.println("No auction lots have been added.");
            return;
        }

        lots = auctionSystem.sortLotsByTitleAscending(lots);
        displayAuctionLotResults(lots);
    }

    private void displayAllBidders() {
        LinkedList<Bidder> bidders = auctionSystem.getBidders();

        if (bidders.isEmpty()) {
            System.out.println("No bidders have been added.");
            return;
        }

        bidders = auctionSystem.sortBiddersByNameAscending(bidders);
        displayBidderResults(bidders);
    }

    private void displayAllBids() {
        LinkedList<Bid> bids = auctionSystem.getBids();

        if (bids.isEmpty()) {
            System.out.println("No bids have been placed.");
            return;
        }

        System.out.println("\nALL BIDS");

        for (int i = 0; i < bids.size(); i++) {
            System.out.println("\n-------------------------");
            System.out.println(bids.get(i));
        }
    }

    private void displaySystemSummary() {
        System.out.println("\nAUCTION SYSTEM SUMMARY");
        System.out.println(auctionSystem);
    }

    private void displayAuctionLotResults(LinkedList<AuctionLot> lots) {
        System.out.println("\nAUCTION LOT RESULTS");

        for (int i = 0; i < lots.size(); i++) {
            AuctionLot lot = lots.get(i);
            System.out.println(lot.getLotID() + " | " + lot.getTitle() + " | " + lot.getType() + " | " + lot.getEstimatedYear() + " | €" + lot.getGuidePrice() + " | " + lot.getStatusDescription());
        }
    }

    private void displayBidderResults(LinkedList<Bidder> bidders) {
        System.out.println("\nBIDDER RESULTS");

        for (int i = 0; i < bidders.size(); i++) {
            Bidder bidder = bidders.get(i);
            System.out.println(bidder.getBidderID() + " | " + bidder.getName() + " | " + bidder.getAddress() + " | " + bidder.getTelephone() + " | " + bidder.getEmail());
        }
    }

    // --------------------------------------------------
    // Persistence
    // --------------------------------------------------

    private void saveSystem() {
        try {
            persistence.save(auctionSystem);

            System.out.println("Auction data saved successfully " + "to " + persistence.getFileName());
        }

        catch (IOException exception) {
            System.out.println("Auction data could not be saved: " + exception.getMessage());
        }

        catch (RuntimeException exception) {
            System.out.println("Persistence error while saving: " + exception.getMessage());
        }
    }

    private void loadSystem() {
        if (!persistence.fileExists()) {
            System.out.println("No saved auction file was found.");
            return;
        }

        boolean confirmed = readBoolean("Loading replaces the current auction data. Continue? (yes/no): ");

        if (!confirmed) {
            System.out.println("Load cancelled.");
            return;
        }

        try {
            AuctionSystem loadedSystem = persistence.loadAuctionSystem();
            auctionSystem = loadedSystem;

            System.out.println("Auction data loaded successfully.");
            displaySystemSummary();
        }

        catch (IOException exception) {
            System.out.println("Auction data could not be loaded: " + exception.getMessage());
        }

        catch (RuntimeException exception) {
            System.out.println("The saved file is invalid: " + exception.getMessage());
        }
    }

    private void loadAutomatically() {
        if (!persistence.fileExists()) {
            System.out.println("No previous save file found. A new auction system has been created.");
            return;
        }

        try {
            auctionSystem = persistence.loadAuctionSystem();
            System.out.println("Previous auction data loaded " + "from " + persistence.getFileName());
            System.out.println(auctionSystem);
        }

        catch (IOException | RuntimeException exception) {
            System.out.println("Previous auction data could not be loaded.");
            System.out.println("Reason: " + exception.getMessage());
            System.out.println("A new empty auction system will be used.");
            auctionSystem = new AuctionSystem();
        }
    }

    private void saveAutomatically() {
        try {
            persistence.save(auctionSystem);

            System.out.println("Auction data saved automatically " + "to " + persistence.getFileName());
        }

        catch (IOException | RuntimeException exception) {
            System.out.println("Warning: auction data could not be saved on exit.");
            System.out.println("Reason: " + exception.getMessage());
        }
    }

    // --------------------------------------------------
    // Reset
    // --------------------------------------------------

    private void resetSystem() {
        boolean confirmed = readBoolean("Clear all auction lots, bidders and bids? (yes/no): ");

        if (!confirmed) {
            System.out.println("Reset cancelled.");
            return;
        }

        auctionSystem.clear();
        System.out.println("All auction data has been cleared.");
    }

    // --------------------------------------------------
    // Input methods
    // --------------------------------------------------

    private String readString(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private String readRequiredString(String prompt) {
        while (true) {
            String value = readString(prompt);

            if (!value.isBlank()) {
                return value;
            }

            System.out.println("A value must be entered.");
        }
    }

    private int readInteger(String prompt) {
        while (true) {
            String value = readString(prompt);

            try {
                return Integer.parseInt(value);
            }

            catch (NumberFormatException exception) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private int readPositiveInteger(String prompt) {

        while (true) {
            int value = readInteger(prompt);

            if (value > 0) {
                return value;
            }

            System.out.println("The value must be greater " + "than zero.");
        }
    }

    private Integer readOptionalInteger(String prompt) {
        while (true) {
            String value = readString(prompt);

            if (value.isBlank()) {
                return null;
            }

            try {
                return Integer.parseInt(value);
            }

            catch (NumberFormatException exception) {
                System.out.println("Enter a whole number or press Enter for any.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String value = readString(prompt);

            try {
                return Double.parseDouble(value);
            }

            catch (NumberFormatException exception) {
                System.out.println("Enter a valid number.");
            }
        }
    }

    private double readPositiveDouble(String prompt) {

        while (true) {
            double value = readDouble(prompt);

            if (!Double.isNaN(value) && !Double.isInfinite(value) && value > 0) {
                return value;
            }

            System.out.println("The value must be a valid number greater than zero.");
        }
    }

    private double readNonNegativeDouble(String prompt) {

        while (true) {
            double value = readDouble(prompt);

            if (!Double.isNaN(value) && !Double.isInfinite(value) && value >= 0) {
                return value;
            }

            System.out.println("The value must be a valid non-negative number.");
        }
    }

    private boolean readBoolean(String prompt) {
        while (true) {
            String value = readString(prompt);

            if (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("true")) {
                return true;
            }

            if (value.equalsIgnoreCase("no") || value.equalsIgnoreCase("n") || value.equalsIgnoreCase("false")) {
                return false;
            }

            System.out.println("Enter yes or no.");
        }
    }
}