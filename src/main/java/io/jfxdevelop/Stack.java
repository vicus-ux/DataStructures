package io.jfxdevelop;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;



public class Stack<T> implements Iterable<T> {
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node<T> top;
    private int size;
    
    public Stack() {
        this.top = null;
        this.size = 0;
    }
    

    public void push(T element) {
        Node<T> newNode = new Node<>(element);
        newNode.next = top;
        top = newNode;
        size++;
    }
    

    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        
        T removedData = top.data;
        top = top.next;
        size--;
        
        return removedData;
    }

    public T top() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void clear() {
        top = null;
        size = 0;
    }
    
    public int search(T element) {
        Node<T> current = top;
        int position = 1;
        
        while (current != null) {
            if ((element == null && current.data == null) ||
                (element != null && element.equals(current.data))) {
                return position;
            }
            current = current.next;
            position++;
        }
        
        return -1;
    }
    
    public boolean contains(T element) {
        return search(element) != -1;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = top;
            
            @Override
            public boolean hasNext() {
                return current != null;
            }
            
            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stack(top to bottom): [");
        
        Node<T> current = top;
        boolean first = true;
        while (current != null) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.data);
            current = current.next;
            first = false;
        }
        
        sb.append("]");
        return sb.toString();
    }
}