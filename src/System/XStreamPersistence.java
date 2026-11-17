package System;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class XStreamPersistence {
    private static final String FILE_NAME = "auction-data.xml";

    private XStream createXStream() {
        XStream xstream = new XStream(new DomDriver());

        xstream.alias("systemData", SystemData.class);
        xstream.alias("auctionSystem", AuctionSystem.class);
        xstream.alias("auctionLot", AuctionLot.class);
        xstream.alias("bidder", Bidder.class);
        xstream.alias("bid", Bid.class);
        xstream.alias("hashTable", HashTable.class);
        xstream.alias("hashEntry", HashEntry.class);
        xstream.alias("linkedList", LinkedList.class);
        xstream.registerConverter(new SingleValueConverter() {

            @Override
            public boolean canConvert(Class type) {
                return type == LocalDateTime.class;
            }

            @Override
            public String toString(Object object) {
                LocalDateTime dateTime = (LocalDateTime) object;
                return dateTime.toString();
            }

            @Override
            public Object fromString(String text) {
                return LocalDateTime.parse(text);
            }
        });

        xstream.allowTypesByWildcard(new String[]{"auction.**"});
        xstream.allowTypes(new Class[]{SystemData.class, AuctionSystem.class, AuctionLot.class, Bidder.class, Bid.class, HashTable.class, HashEntry.class, LinkedList.class, LocalDateTime.class});
        return xstream;
    }

    // --------------------------------------------------
    // Save
    // --------------------------------------------------

    public void save(SystemData data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("System data cannot be null.");
        }

        XStream xstream = createXStream();

        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            xstream.toXML(data, writer);
        }
    }

    public void save(AuctionSystem auctionSystem) throws IOException {
        if (auctionSystem == null) {
            throw new IllegalArgumentException("Auction system cannot be null.");
        }

        SystemData data = new SystemData(auctionSystem);
        save(data);
    }

    // --------------------------------------------------
    // Load
    // --------------------------------------------------

    public SystemData load() throws IOException {
        if (!fileExists()) {
            throw new IOException("The save file does not exist.");
        }
        XStream xstream = createXStream();

        try (FileReader reader = new FileReader(FILE_NAME)) {
            Object loadedObject = xstream.fromXML(reader);

            if (!(loadedObject instanceof SystemData)) {
                throw new IOException("The file does not contain valid auction data.");
            }
            return (SystemData) loadedObject;
        }
    }

    public AuctionSystem loadAuctionSystem() throws IOException {
        SystemData data = load();
        AuctionSystem loadedSystem = data.getAuctionSystem();

        if (loadedSystem == null) {
            throw new IOException("The loaded auction system is null.");
        }
        return loadedSystem;
    }

    // --------------------------------------------------
    // File information
    // --------------------------------------------------

    public boolean fileExists() {
        File file = new File(FILE_NAME);
        return file.exists() && file.isFile();
    }

    public String getFileName() {
        return FILE_NAME;
    }

    public long getFileSize() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    public boolean deleteSaveFile() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }
}