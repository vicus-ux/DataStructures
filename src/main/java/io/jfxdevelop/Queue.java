package io.jfxdevelop;


import java.util.Iterator;

public class Queue<T> implements Iterable<T>{
    private static class Node<T>{
        T data;
        Node<T> next;

        Node(T data){
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public Queue(){
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public void enqueue(T element){
        if (element == null) throw new IllegalArgumentException("Cannot enqueue null element");
        Node<T> node = new Node<>(element);
        if (rear == null) {
            front = node;
            rear = node;
        } else {
            rear.next = node;
            rear = node;
        }
        size++;
    }

    public void add(T element){
        enqueue(element);
    }
    
    public boolean empty(){
        return size == 0;
    }
    
    public int size(){
        return size;
    }
    


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = front;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T value = current.data;
                current = current.next;
                return value;
            }
        };
    }

    


}
