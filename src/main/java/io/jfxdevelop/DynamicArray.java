package io.jfxdevelop;

import java.util.Iterator;
//import java.util.Arrays;
//import java.util.NoSuchElementException;

public class DynamicArray<T> implements Iterable<T> {

    private T[] array;
    private int size;
    private int capacity;
    private static final int DEFAULT_CAPACITY = 10;


    public DynamicArray() {
        @SuppressWarnings("unchecked")
        T[] initial = (T[]) new Object[DEFAULT_CAPACITY];
        this.array = initial;
    
        this.size = 0;
        this.capacity = DEFAULT_CAPACITY;
    }

    public DynamicArray(int initCapacity) {

        if (initCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }

        @SuppressWarnings("unchecked")
        T[] initial = (T[]) new Object[initCapacity];
        array = initial;
        size = 0;
        this.capacity = initCapacity;
    }
    
    public void add(T element) {
        if (size == capacity) {
            resize();
        }
        array[size++] = element;
    }
    
    public void resize() {
        if(size >=   capacity) capacity = Math.max(capacity * 2, DEFAULT_CAPACITY);
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) new Object[capacity];
        System.arraycopy(array, 0, newArray, 0, size);
        array = newArray;
    }
    public void insert(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if (size == capacity) {
            resize();
        }
        System.arraycopy(array, index, array, index + 1, size - index);
        array[index] = element;
        size++;
    }
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        T removedElement = array[index];
        int numberToMove =  size - index - 1;
        System.arraycopy(array, index + 1, array, index, numberToMove);
        array[size - 1] = null;
        size--;
        return removedElement;
    }
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        return array[index];
    }
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        array[index] = element;
    }
    public int size() {
        return size;
    }
    
    public int capacity() {
        return capacity;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public void clear() {
        for(int i = 0; i < size; i++){
            array[i] = null;
        }
        size = 0;
    }
    public int indexOf(T element) {
        for(int i = 0; i < size; i++){
            if(array[i].equals(element)){
                return i;
            }
        }
        return -1;
    }
    public boolean contains(T element) {
        return indexOf(element) != -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            public boolean hasNext() {
                return index < size;
            }
            public T next() {
                return array[index++];
            }
        };
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i < size; i++){
            sb.append(array[i]);
            if(i < size - 1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
  