package org.relgames.test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author opoleshuk
 */
public class Node<T> {
    public final T o;
    public Node<T> next;

    public Node(T o) {
        this.o = o;
        int c = count.incrementAndGet();
        if (c%10000000==0) {
            System.out.printf("So far created %dm objects\n", c/1000000);
        }
    }

    private final static AtomicInteger count = new AtomicInteger();

    public static int getCount() {
        return count.get();
    }

    @Override
    public String toString() {
        return o.toString();
    }
}
