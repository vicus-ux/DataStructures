package io.jfxdevelop;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Реализация хеш-таблицы с методом цепочек для разрешения коллизий
 * Поддерживает основные операции put, get, remove, содержит итераторы
 * делает красивый вывод статистики таблицы
 * 
 * @param <K> тип ключей
 * @param <V> тип значений
 */
public class HashTable<K, V> implements Iterable<K> {
    
    /**
     * Внутренний класс для представления элемента хеш-таблицы
     */
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        
        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
        
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
    
    // Основные поля
    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;
    private int resizeCount;
    private int collisionCount;
    private int maxChainLength;
    
    // Константы
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    private static final int MAX_CAPACITY = 1 << 30;
    
    /**
     * Конструктор по умолчанию
     */
    @SuppressWarnings("unchecked")
    public HashTable() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.resizeCount = 0;
        this.collisionCount = 0;
        this.maxChainLength = 0;
    }
    
    /**
     * Конструктор с заданной начальной емкостью
     * @param initialCapacity начальная емкость
     * @throws IllegalArgumentException если емкость <= 0
     */
    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        this.capacity = findNextPowerOfTwo(initialCapacity);
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.resizeCount = 0;
        this.collisionCount = 0;
        this.maxChainLength = 0;
    }
    
    /**
     * Конструктор из существующей Map
     * @param map исходная Map
     */
    public HashTable(Map<? extends K, ? extends V> map) {
        this(Math.max((int) (map.size() / LOAD_FACTOR) + 1, DEFAULT_CAPACITY));
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Вычисление хеша для ключа
     * @param key ключ
     * @return индекс в массиве buckets
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        // Распределение хеша для лучшего распределения
        return (h ^ (h >>> 16)) & (capacity - 1);
    }
    
    /**
     * Добавление пары ключ-значение
     * @param key ключ
     * @param value значение
     * @throws IllegalArgumentException если ключ null
     */
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        int index = hash(key);
        Entry<K, V> current = buckets[index];
        
        // Проверка существования ключа
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        // Добавление нового элемента в начало цепочки
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;
        
        // Подсчет коллизий
        if (buckets[index] != null && buckets[index].next != null) {
            collisionCount++;
        }
        
        // Обновление максимальной длины цепочки
        updateMaxChainLength(index);
        
        // Проверка необходимости resize
        if ((double) size / capacity > LOAD_FACTOR) {
            resize();
        }
    }
    
    /**
     * Получение значения по ключу
     * @param key ключ
     * @return значение или null если ключ не найден
     * @throws IllegalArgumentException если ключ null
     */
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
    
    /**
     * Получение значения по ключу или значения по умолчанию
     * @param key ключ
     * @param defaultValue значение по умолчанию
     * @return значение или defaultValue если ключ не найден
     */
    public V getOrDefault(K key, V defaultValue) {
        V value = get(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Удаление элемента по ключу
     * @param key ключ
     * @return удаленное значение или null если ключ не найден
     * @throws IllegalArgumentException если ключ null
     */
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
    
    /**
     * Проверка наличия ключа
     * @param key ключ
     * @return true если ключ существует
     */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        return get(key) != null;
    }
    
    /**
     * Проверка наличия значения
     * @param value значение
     * @return true если значение существует
     */
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
    
    /**
     * @return количество элементов
     */
    public int size() {
        return size;
    }
    
    /**
     * @return true если таблица пуста
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Очистка таблицы
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.collisionCount = 0;
        this.maxChainLength = 0;
    }
    
    /**
     * Получение всех ключей
     * @return список ключей
     */
    public List<K> keys() {
        List<K> keys = new ArrayList<>();
        for (K key : this) {
            keys.add(key);
        }
        return keys;
    }
    
    /**
     * Получение всех значений
     * @return список значений
     */
    public List<V> values() {
        List<V> values = new ArrayList<>();
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                values.add(current.value);
                current = current.next;
            }
        }
        return values;
    }
    
    /**
     * Получение всех записей
     * @return список записей
     */
    public List<Map.Entry<K, V>> entries() {
        List<Map.Entry<K, V>> entries = new ArrayList<>();
        for (Entry<K, V> bucket : buckets) {
            Entry<K, V> current = bucket;
            while (current != null) {
                entries.add(new AbstractMap.SimpleEntry<>(current.key, current.value));
                current = current.next;
            }
        }
        return entries;
    }
    
    /**
     * Итератор по ключам
     * @return итератор
     */
    @Override
    public Iterator<K> iterator() {
        return new KeyIterator();
    }
    
    /**
     * Итератор по значениям
     * @return итерируемый объект
     */
    public Iterable<V> valuesIterable() {
        return () -> new ValueIterator();
    }
    
    /**
     * Итератор по записям
     * @return итерируемый объект
     */
    public Iterable<Map.Entry<K, V>> entriesIterable() {
        return () -> new EntryIterator();
    }
    
    /**
     * Поток ключей
     * @return поток ключей
     */
    public Stream<K> keyStream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size, 0), false);
    }
    
    /**
     * Поток значений
     * @return поток значений
     */
    public Stream<V> valueStream() {
        return StreamSupport.stream(Spliterators.spliterator(valuesIterable().iterator(), size, 0), false);
    }
    
    /**
     * Поток записей
     * @return поток записей
     */
    public Stream<Map.Entry<K, V>> entryStream() {
        return StreamSupport.stream(Spliterators.spliterator(entriesIterable().iterator(), size, 0), false);
    }
    
    /**
     * Изменение размера таблицы при превышении load factor
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        if (capacity >= MAX_CAPACITY) {
            return;
        }
        
        int oldCapacity = capacity;
        capacity *= 2;
        Entry<K, V>[] oldBuckets = buckets;
        buckets = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
        collisionCount = 0;
        maxChainLength = 0;
        
        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> current = oldBuckets[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                put(current.key, current.value);
                current = next;
            }
        }
        
        resizeCount++;
    }
    
    /**
     * Поиск следующей степени двойки
     * @param value исходное значение
     * @return степень двойки
     */
    private int findNextPowerOfTwo(int value) {
        int power = 1;
        while (power < value && power < MAX_CAPACITY) {
            power <<= 1;
        }
        return power;
    }
    
    /**
     * Обновление максимальной длины цепочки
     * @param index индекс бакета
     */
    private void updateMaxChainLength(int index) {
        int length = 0;
        Entry<K, V> current = buckets[index];
        while (current != null) {
            length++;
            current = current.next;
        }
        maxChainLength = Math.max(maxChainLength, length);
    }
    
    /**
     * Вывод статистики таблицы
     */
    public void printStatistics() {
        int[] chainLengths = new int[capacity];
        int nonEmptyBuckets = 0;
        int totalChainLength = 0;
        
        for (int i = 0; i < capacity; i++) {
            int length = 0;
            Entry<K, V> current = buckets[i];
            while (current != null) {
                length++;
                current = current.next;
            }
            chainLengths[i] = length;
            if (length > 0) nonEmptyBuckets++;
            totalChainLength += length;
        }
        
        double avgChainLength = nonEmptyBuckets > 0 ? (double) totalChainLength / nonEmptyBuckets : 0;
        double loadFactor = (double) size / capacity;
        
        System.out.println("=== HashTable Statistics ===");
        System.out.println("Size: " + size);
        System.out.println("Capacity: " + capacity);
        System.out.println("Load factor: " + String.format("%.2f", loadFactor));
        System.out.println("Non-empty buckets: " + nonEmptyBuckets + "/" + capacity);
        System.out.println("Max chain length: " + maxChainLength);
        System.out.println("Average chain length: " + String.format("%.2f", avgChainLength));
        System.out.println("Collisions: " + collisionCount);
        System.out.println("Resizes: " + resizeCount);
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
    
    /**
     * Базовый итератор для хеш-таблицы
     */
    private abstract class HashTableIterator<T> implements Iterator<T> {
        protected int bucketIndex = 0;
        protected Entry<K, V> currentEntry = null;
        protected int entriesVisited = 0;
        
        HashTableIterator() {
            findNextEntry();
        }
        
        protected void findNextEntry() {
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
        
        protected void moveToNext() {
            entriesVisited++;
            currentEntry = currentEntry.next;
            if (currentEntry == null) {
                bucketIndex++;
                findNextEntry();
            }
        }
    }
    
    /**
     * Итератор по ключам
     */
    private class KeyIterator extends HashTableIterator<K> {
        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            K key = currentEntry.key;
            moveToNext();
            return key;
        }
    }
    
    /**
     * Итератор по значениям
     */
    private class ValueIterator extends HashTableIterator<V> {
        @Override
        public V next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            V value = currentEntry.value;
            moveToNext();
            return value;
        }
    }
    
    /**
     * Итератор по записям
     */
    private class EntryIterator extends HashTableIterator<Map.Entry<K, V>> {
        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<>(currentEntry.key, currentEntry.value);
            moveToNext();
            return entry;
        }
    }
    
    // Методы для тестирования и отладки
    
    /**
     * Получить текущую емкость (для тестирования)
     * @return емкость
     */
    int getCapacity() {
        return capacity;
    }
    
    /**
     * Получить количество коллизий (для тестирования)
     * @return количество коллизий
     */
    int getCollisionCount() {
        return collisionCount;
    }
    
    /**
     * Получить максимальную длину цепочки (для тестирования)
     * @return максимальная длина цепочки
     */
    int getMaxChainLength() {
        return maxChainLength;
    }
}