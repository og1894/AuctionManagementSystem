package System;

import java.util.Objects;

public class HashEntry<V> {
    private final int key;
    private V value;

    public HashEntry(int key, V value) {
        if (key < 1) {
            throw new IllegalArgumentException("Hash-entry key must be greater than zero.");
        }

        if (value == null) {
            throw new IllegalArgumentException("Hash-entry value cannot be null.");
        }
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        if (value == null) {
            throw new IllegalArgumentException("Hash-entry value cannot be null.");
        }
        this.value = value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof HashEntry<?> otherEntry)) {
            return false;
        }
        return key == otherEntry.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Key: " + key + "\nValue: " + value;
    }
}