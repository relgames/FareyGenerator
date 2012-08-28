package org.relgames.test;

import org.apache.commons.math3.fraction.Fraction;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author opoleshuk
 */
public class ExecutorFarey {
    private static final int DIVIDE_COUNT = 2;
    private static final int THREAD_COUNT = 1<<DIVIDE_COUNT;

    public static class FareyTask {
        public final Node<Fraction> leftNode;
        public final Node<Fraction> rightNode;

        public FareyTask(Node<Fraction> leftNode, Node<Fraction> rightNode) {
            this.leftNode = leftNode;
            this.rightNode = rightNode;
        }
    }

    private static ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    public static List<Fraction> getFareyList() {
        final Node<Fraction> leftNode = new Node<>(new Fraction(0, 1));
        leftNode.next = new Node<>(new Fraction(1, 1));

        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        for (int i=0; i<DIVIDE_COUNT; i++) {
            for (final Node<Fraction> n: asNodeList(leftNode)) {
                if (n.next!=null) {
                    divide(n, n.next);
                }
            }
        }

        for (final Node<Fraction> n: asNodeList(leftNode)) {
            if (n.next!=null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Queue<FareyTask> q = new LinkedList<>();
                        q.add(new FareyTask(n, n.next));

                        while (q.size()>0) {
                            FareyTask task = q.remove();
                            Node<Fraction> mediantNode = divide(task.leftNode, task.rightNode);
                            if (mediantNode!=null) {
                                q.add(new FareyTask(task.leftNode, mediantNode));
                                q.add(new FareyTask(mediantNode, task.rightNode));
                            }

                        }

                        countDownLatch.countDown();
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

    private static List<Fraction> asList(Node<Fraction> firstNode) {
        List<Fraction> result = new LinkedList<>();
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

    public static Node<Fraction> divide(Node<Fraction> leftNode, Node<Fraction> rightNode) {
        Fraction mediant = new Fraction(leftNode.o.getNumerator()+ rightNode.o.getNumerator(),
                leftNode.o.getDenominator()+ rightNode.o.getDenominator());
        if (mediant.getDenominator()> BASE) {
            return null;
        }

        Node<Fraction> mediantNode = new Node<>(mediant);
        leftNode.next = mediantNode;
        mediantNode.next = rightNode;

        return mediantNode;
    }


    private final static int BASE = 500;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        List<Fraction> farey = getFareyList();
        System.out.printf("Total %d in  %dms", farey.size(), System.currentTimeMillis()-time);
    }

}
