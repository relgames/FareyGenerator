package org.relgames.test;

/**
 * @author opoleshuk
 */
public class Node<T> {
    public final T o;
    public Node<T> next;

    public Node(T o) {
        this.o = o;
    }

    @Override
    public String toString() {
        return o.toString();
    }
}
