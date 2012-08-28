package org.relgames.test;

import org.apache.commons.math3.fraction.Fraction;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author opoleshuk
 */
public class ExecutorFarey {
    private static final int DIVIDE_COUNT = 2;
    private static final int THREAD_COUNT = 1<<DIVIDE_COUNT;

    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    private static List<Fraction> getFareyList() {
        final Node<Fraction> leftNode = new Node<>(new Fraction(0, 1));
        leftNode.next = new Node<>(new Fraction(1, 1));

        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i=0; i<DIVIDE_COUNT; i++) {
            for (final Node<Fraction> n: asNodeList(leftNode)) {
                if (n.next!=null) {
                    divide(n);
                }
            }
        }

        for (final Node<Fraction> n: asNodeList(leftNode)) {
            if (n.next!=null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        recursiveDivide(n);
                        countDownLatch.countDown();
                    }

                    private void recursiveDivide(Node<Fraction> leftNode) {
                        if (divide(leftNode)) {
                            Node<Fraction> mediant = leftNode.next;
                            recursiveDivide(leftNode);
                            recursiveDivide(mediant);
                        }
                    }
                });

            }
        }


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        executor.shutdown();

        return asList(leftNode);
    }

    public static List<Fraction> asList(Node<Fraction> firstNode) {
        List<Fraction> result = new ArrayList<>(Node.getCount());
        Node<Fraction> currentNode = firstNode;
        while (currentNode!=null) {
            result.add(currentNode.o);
            currentNode = currentNode.next;
        }
        return result;
    }

    private static List<Node<Fraction>> asNodeList(Node<Fraction> firstNode) {
        List<Node<Fraction>> result = new LinkedList<>();
        Node<Fraction> currentNode = firstNode;
        while (currentNode!=null) {
            result.add(currentNode);
            currentNode = currentNode.next;
        }
        return result;
    }

    private static boolean divide(Node<Fraction> leftNode) {
        Node<Fraction> rightNode = leftNode.next;

        int newDenominator = leftNode.o.getDenominator()+rightNode.o.getDenominator();
        if (newDenominator>BASE) {
            return false;
        }

        Fraction mediant = new Fraction(leftNode.o.getNumerator()+ rightNode.o.getNumerator(),
                newDenominator);

        Node<Fraction> mediantNode = new Node<>(mediant);
        leftNode.next = mediantNode;
        mediantNode.next = rightNode;

        return true;
    }


    public final static int BASE = ForkJoinFarey.BASE;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        List<Fraction> farey = getFareyList();
        System.out.printf("Total %d in  %dms", farey.size(), System.currentTimeMillis()-time);
    }

}
