package io.jfxdevelop;

import java.util.Iterator;
import java.util.Stack;

public class BinarySearchTree <T extends Comparable<T>> implements Iterable<T> {
    private static class Node<T> {
        Node<T> left;
        Node<T> right;
        T data;

        Node(T data){
            this.data = data;
            this.left = null;
            this.right = null;
        }
            
    }

    private Node<T> root;
    private int size;

    public BinarySearchTree(){
        this.root = null;
        this.size = 0;
    }

    public void insert(T element){
        if(element == null) throw new IllegalArgumentException("Cannot be insert element null");
        root = insertRecursive(root, element);

    }

    private Node<T> insertRecursive(Node<T> current, T element){
        if (current == null) return new Node<>(element);
        int cmp = element.compareTo(current.data);
        if (cmp < 0) {
            current.left = insertRecursive(current.left, element);
        } else if (cmp > 0) {
            current.right = insertRecursive(current.right, element);
        } else {
            // duplicate, do nothing
            return current;
        }
        return current;
    }

    public boolean contains(T element){
        return containsRecursive(root, element);
    }
    private boolean containsRecursive(Node<T> current, T element){
        if (current == null) return false;
        int cmp = element.compareTo(current.data);
        if (cmp < 0) return containsRecursive(current.left, element);
        else if (cmp > 0) return containsRecursive(current.right, element);
        else return true;
    }   

    public void remove(T element){
        root = removeRecursive(root, element);
    }
    private Node<T> removeRecursive(Node<T> current, T element){
        if (current == null) return null;
        int cmp = element.compareTo(current.data);
        if (cmp < 0) current.left = removeRecursive(current.left, element);
        else if (cmp > 0) current.right = removeRecursive(current.right, element);
        else {
            if (current.left == null) return current.right;
            else if (current.right == null) return current.left;
            else {
                current.data = findMin(current.right);
                current.right = removeRecursive(current.right, current.data);
            }
        }
        return current;
    }

    private T findMin(Node<T> current){
        while (current.left != null) current = current.left;
        return current.data;
    }
    private T findMin() {
        return findMinRecursive(root);
    }
    private T findMinRecursive(Node<T> current){
        if (current.left == null) return current.data;
        return findMinRecursive(current.left);
    }
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }   
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(T element : this){
            sb.append(element);
            sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Stack<Node<T>> stack = new Stack<>();

            {
                pushLeft(root);
            }

            private void pushLeft(Node<T> node) {
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                Node<T> node = stack.pop();
                if (node.right != null) pushLeft(node.right);
                return node.data;
            }
        };
    }

}
