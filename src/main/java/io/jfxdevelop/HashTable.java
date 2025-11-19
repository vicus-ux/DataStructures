package io.jfxdevelop;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashTable<K, V> implements Iterable<K> {
    
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    
    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
    }
    
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        this.capacity = initialCapacity;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
    }
    
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode()) % capacity;
    }
    
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = hash(key);
        Entry<K, V> current = buckets[index];
        
        // Check if key already exists
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        // Add new entry at the beginning of the chain
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
        
        // Resize if load factor is exceeded
        if ((double) size / capacity > LOAD_FACTOR) {
            resize();
        }
    }
    
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = hash(key);
        Entry<K, V> current = buckets[index];
        
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        
        return null;
    }
    
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = hash(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> previous = null;
        
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        
        return null;
    }
    
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return get(key) != null;
    }
    
    public boolean containsValue(V value) {
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                if (value == null ? current.value == null : current.value.equals(value)) {
                    return true;
                }
                current = current.next;
            }
        }
        return false;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            buckets[i] = null;
        }
        size = 0;
    }
    
    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity = capacity;
        capacity *= 2;
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
        
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> current = oldBuckets[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                int newIndex = hash(current.key);
                current.next = buckets[newIndex];
                buckets[newIndex] = current;
                size++;
                current = next;
            }
        }
    }
    
    @Override
    public Iterator<K> iterator() {
        return new Iterator<K>() {
            private int bucketIndex = 0;
            private Entry<K, V> currentEntry = null;
            private int entriesVisited = 0;
            
            {
                findNextEntry();
            }
            
            private void findNextEntry() {
                while (bucketIndex < capacity && buckets[bucketIndex] == null) {
                    bucketIndex++;
                }
                if (bucketIndex < capacity) {
                    currentEntry = buckets[bucketIndex];
                } else {
                    currentEntry = null;
                }
            }
            
            @Override
            public boolean hasNext() {
                return entriesVisited < size;
            }
            
            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                
                K key = currentEntry.key;
                entriesVisited++;
                
                currentEntry = currentEntry.next;
                if (currentEntry == null) {
                    bucketIndex++;
                    findNextEntry();
                }
                
                return key;
            }
        };
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.key).append("=").append(current.value);
                first = false;
                current = current.next;
            }
        }
        
        sb.append("}");
        return sb.toString();
    }      
}
