package System;

public class HashTable<V> {
    private LinkedList<HashEntry<V>>[] buckets;
    private int size;
    private final int initialCapacity;
    private static final int DEFAULT_CAPACITY = 11;
    private static final double MAXIMUM_LOAD_FACTOR = 0.75;

    // --------------------------------------------------
    // Constructors
    // --------------------------------------------------

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than zero.");
        }

        this.initialCapacity = initialCapacity;
        buckets = (LinkedList<HashEntry<V>>[]) new LinkedList[initialCapacity];
        createBuckets();
        size = 0;
    }

    private void createBuckets() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList<>();
        }
    }

    // --------------------------------------------------
    // Basic information
    // --------------------------------------------------

    public int size() {
        return size;
    }

    public int capacity() {
        return buckets.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public double getLoadFactor() {
        return (double) size / buckets.length;
    }

    // --------------------------------------------------
    // Custom hash function
    // --------------------------------------------------

    private int hash(int key) {
        int mixedKey = key;
        mixedKey = mixedKey ^ (mixedKey >>> 16);
        mixedKey = mixedKey * 31;
        mixedKey = mixedKey ^ (mixedKey >>> 7);
        return Math.floorMod(mixedKey, buckets.length);
    }

    // --------------------------------------------------
    // Add or replace
    // --------------------------------------------------

    public boolean put(int key, V value) {
        validateKey(key);
        validateValue(value);
        int bucketIndex = hash(key);
        LinkedList<HashEntry<V>> bucket = buckets[bucketIndex];

        for (int i = 0; i < bucket.size(); i++) {
            HashEntry<V> entry = bucket.get(i);

            if (entry.getKey() == key) {
                entry.setValue(value);
                return false;
            }
        }
        bucket.add(new HashEntry<>(key, value));
        size++;

        if (getLoadFactor() > MAXIMUM_LOAD_FACTOR) {
            resize();
        }
        return true;
    }

    public void add(int key, V value) {
        validateKey(key);
        validateValue(value);

        if (containsKey(key)) {
            throw new IllegalArgumentException("Key " + key + " already exists.");
        }
        put(key, value);
    }

    // --------------------------------------------------
    // Retrieve
    // --------------------------------------------------

    public V get(int key) {
        validateKey(key);
        int bucketIndex = hash(key);
        LinkedList<HashEntry<V>> bucket = buckets[bucketIndex];

        for (int i = 0; i < bucket.size(); i++) {
            HashEntry<V> entry = bucket.get(i);

            if (entry.getKey() == key) {
                return entry.getValue();
            }
        }
        return null;
    }

    public V getRequired(int key) {
        V value = get(key);

        if (value == null) {
            throw new IllegalArgumentException("No value exists for key " + key + ".");
        }
        return value;
    }

    public boolean containsKey(int key) {
        return get(key) != null;
    }

    // --------------------------------------------------
    // Remove
    // --------------------------------------------------

    public V remove(int key) {
        validateKey(key);
        int bucketIndex = hash(key);
        LinkedList<HashEntry<V>> bucket = buckets[bucketIndex];

        for (int i = 0; i < bucket.size(); i++) {
            HashEntry<V> entry = bucket.get(i);

            if (entry.getKey() == key) {
                HashEntry<V> removedEntry = bucket.remove(i);
                size--;
                return removedEntry.getValue();
            }
        }
        return null;
    }

    // --------------------------------------------------
    // Return all values
    // --------------------------------------------------

    public LinkedList<V> values() {
        LinkedList<V> values = new LinkedList<>();

        for (int bucketIndex = 0; bucketIndex < buckets.length; bucketIndex++) {
            LinkedList<HashEntry<V>> bucket = buckets[bucketIndex];

            for (int entryIndex = 0; entryIndex < bucket.size(); entryIndex++) {
                HashEntry<V> entry = bucket.get(entryIndex);
                values.add(entry.getValue());
            }
        }
        return values;
    }

    public LinkedList<Integer> keys() {
        LinkedList<Integer> keys = new LinkedList<>();

        for (int bucketIndex = 0; bucketIndex < buckets.length; bucketIndex++) {
            LinkedList<HashEntry<V>> bucket = buckets[bucketIndex];

            for (int entryIndex = 0; entryIndex < bucket.size(); entryIndex++) {
                HashEntry<V> entry = bucket.get(entryIndex);
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    // --------------------------------------------------
    // Clear
    // --------------------------------------------------

    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = (LinkedList<HashEntry<V>>[]) new LinkedList[initialCapacity];
        createBuckets();
        size = 0;
    }

    // --------------------------------------------------
    // Resizing
    // --------------------------------------------------

    @SuppressWarnings("unchecked")
    private void resize() {
        LinkedList<HashEntry<V>>[] oldBuckets = buckets;
        int newCapacity = findNextPrime(oldBuckets.length * 2);
        buckets = (LinkedList<HashEntry<V>>[]) new LinkedList[newCapacity];
        createBuckets();

        int previousSize = size;
        size = 0;

        for (int bucketIndex = 0; bucketIndex < oldBuckets.length; bucketIndex++) {
            LinkedList<HashEntry<V>> oldBucket = oldBuckets[bucketIndex];

            for (int entryIndex = 0; entryIndex < oldBucket.size(); entryIndex++) {
                HashEntry<V> entry = oldBucket.get(entryIndex);
                insertWithoutResize(entry.getKey(), entry.getValue());
            }
        }

        if (size != previousSize) {
            throw new IllegalStateException("Entries were lost while resizing.");
        }
    }

    private void insertWithoutResize(int key, V value) {
        int bucketIndex = hash(key);
        buckets[bucketIndex].add(new HashEntry<>(key, value));
        size++;
    }

    // --------------------------------------------------
    // Prime-number helpers
    // --------------------------------------------------

    private int findNextPrime(int number) {
        int candidate = number;

        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }

        if (number == 2) {
            return true;
        }

        if (number % 2 == 0) {
            return false;
        }

        for (int divisor = 3; divisor * divisor <= number; divisor += 2) {
            if (number % divisor == 0) {
                return false;
            }
        }
        return true;
    }

    // --------------------------------------------------
    // Collision information
    // --------------------------------------------------

    public int getBucketSize(int bucketIndex) {
        checkBucketIndex(bucketIndex);
        return buckets[bucketIndex].size();
    }


    public int getLongestChainLength() {
        int longestChain = 0;

        for (int i = 0; i < buckets.length; i++) {
            int currentBucketSize = buckets[i].size();

            if (currentBucketSize > longestChain) {
                longestChain = currentBucketSize;
            }
        }
        return longestChain;
    }

    public int getNumberOfCollisions() {
        int collisions = 0;

        for (int i = 0; i < buckets.length; i++) {
            int bucketSize = buckets[i].size();

            if (bucketSize > 1) {
                collisions += bucketSize - 1;
            }
        }
        return collisions;
    }

    // --------------------------------------------------
    // Validation
    // --------------------------------------------------

    private void validateKey(int key) {
        if (key < 1) {
            throw new IllegalArgumentException("Hash-table key must be greater " + "than zero.");
        }
    }

    private void validateValue(V value) {
        if (value == null) {
            throw new IllegalArgumentException("Hash-table value cannot be null.");
        }
    }

    private void checkBucketIndex(int bucketIndex) {
        if (bucketIndex < 0 || bucketIndex >= buckets.length) {
            throw new IndexOutOfBoundsException("Bucket index: " + bucketIndex + ", Capacity: " + buckets.length);
        }
    }

    // --------------------------------------------------
    // toString
    // --------------------------------------------------

    @Override
    public String toString() {
        return "Hash table size: " + size + "\nCapacity: " + buckets.length + "\nLoad factor: " + getLoadFactor() + "\nCollisions: " + getNumberOfCollisions() + "\nLongest collision chain: " + getLongestChainLength();
    }
}