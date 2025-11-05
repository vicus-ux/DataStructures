package io.jfxdevelop;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        
       Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private int size;
    private Node<T> tail;

    public LinkedList() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);
        newNode.next = head;
        head = newNode;
        if(tail == null) tail = head;
        size++;
    }
    public void addLast(T element) {
        Node<T> newNode = new Node<>(element);
        if(tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }
    public void add(T element){
        addLast(element);
    }
    public void insert(int index, T element) {
        if(index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if(index == 0) {
            addFirst(element);
        } else if(index == size) {
            addLast(element);
        } else {
            Node<T> newNode = new Node<>(element);
            Node<T> current = head;
            for(int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
            size++;
        }
    }
    public T removeFirst() {
        if(head == null) {
            throw new NoSuchElementException("List is empty");
        }
        T data = head.data;
        head = head.next;
        if(head == null) tail = null;
        size--;
        return data;
    }
    public T removeLast() {
        if(tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        T data = tail.data;
        if(head == tail) {
            head = null;
            tail = null;
        } else {
            Node<T> current = head;
            while(current.next != tail) {
                current = current.next;
            }
            current.next = null;
            tail = current;
        }
        size--;
        return data;
    }
    public T remove(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        if(index == 0) {
            return removeFirst();
        } else if(index == size - 1) {
            return removeLast();
        } else {    
            Node<T> current = head;
            for(int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            T data = current.next.data;
            current.next = current.next.next;
            if(current.next == null) tail = current;
            size--;
            return data;
        }
    }
    public T getFirst() {
        if(head == null) {
            throw new NoSuchElementException("List is empty");
        }
        return head.data;
    }
    public T getLast() {
        if(tail == null) {
            throw new NoSuchElementException("List is empty");
        }
        return tail.data;
    }
    public T get(int index) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        Node<T> current = head;
        for(int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    public void set(int index, T element) {
        if(index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        Node<T> current = head;
        for(int i = 0; i < index; i++) {
            current = current.next;
        }
        current.data = element;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    public int indexOf(T element) {
        Node<T> current = head;
        for(int i = 0; i < size; i++) {
            if(current.data.equals(element)) return i;
            current = current.next;
        }
        return -1;
    }
    public boolean contains(T element) {
        return indexOf(element) != -1;
    }

    public void reverse(){
        Node<T> current = head;
        Node<T> previous = null;
        Node<T> next = null;
        while(current != null) {
            next = current.next;
            current.next = previous;
            previous = current;
            current = next;
        }head = previous;
        tail = current;
    }
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;
            public boolean hasNext() {
                return current != null;
            }
            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Node<T> current = head;
        while(current != null) {
            sb.append(current.data);
            current = current.next;
        }
        sb.append("]");
        return sb.toString();   
    }
}
